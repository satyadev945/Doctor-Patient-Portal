package com.hms.db;

import com.hms.util.AwsSecretsManagerUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cloud-ready database connection provider using HikariCP connection pooling
 * combined with Amazon RDS Proxy for optimized database connections.
 *
 * <p>Replaces the previous raw JDBC / DriverManager approach with a managed
 * HikariCP pool.  All sensitive values (host, port, database name, username,
 * password) are resolved from environment variables and AWS Secrets Manager so
 * that no credentials are hard-coded in source code.</p>
 *
 * <h3>Required environment variables</h3>
 * <ul>
 *   <li>{@code DB_HOST}     – RDS / RDS Proxy endpoint (default: {@code localhost})</li>
 *   <li>{@code DB_PORT}     – database port            (default: {@code 3306})</li>
 *   <li>{@code DB_NAME}     – schema / database name   (default: {@code hospital})</li>
 *   <li>{@code DB_SECRET_NAME} – AWS Secrets Manager secret that contains a JSON
 *       object with {@code username} and {@code password} keys.
 *       When this variable is absent the class falls back to the environment
 *       variables {@code DB_USERNAME} and {@code DB_PASSWORD}.</li>
 * </ul>
 *
 * <h3>HikariCP pool settings (tunable via environment variables)</h3>
 * <ul>
 *   <li>{@code DB_POOL_MAX_SIZE}           – maximum pool size           (default: 10)</li>
 *   <li>{@code DB_POOL_MIN_IDLE}           – minimum idle connections    (default: 2)</li>
 *   <li>{@code DB_POOL_CONNECTION_TIMEOUT} – connection-timeout ms       (default: 30000)</li>
 *   <li>{@code DB_POOL_IDLE_TIMEOUT}       – idle-timeout ms             (default: 600000)</li>
 *   <li>{@code DB_POOL_MAX_LIFETIME}       – max-lifetime ms             (default: 1800000)</li>
 *   <li>{@code DB_POOL_INIT_FAIL_TIMEOUT}  – init-fail-timeout ms        (default: 30000)</li>
 *   <li>{@code DB_SOCKET_TIMEOUT_MS}       – JDBC socket timeout ms      (default: 30000)</li>
 * </ul>
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    /** Lazily-initialised, singleton HikariCP data source. */
    private static volatile HikariDataSource dataSource;

    /** Lock object used for double-checked locking during pool initialisation. */
    private static final Object LOCK = new Object();

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Returns a {@link Connection} obtained from the HikariCP connection pool.
     *
     * <p>The pool is initialised on the first call (lazy, thread-safe).  Callers
     * are responsible for closing the returned connection (which returns it to
     * the pool rather than physically closing the underlying socket).</p>
     *
     * @return a live {@link Connection} from the pool, or {@code null} if the
     *         pool could not be initialised
     */
    public static Connection getConn() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to obtain a connection from the HikariCP pool", e);
            return null;
        }
    }

    /**
     * Shuts down the connection pool gracefully.  Should be called during
     * application shutdown (e.g. from a {@code ServletContextListener}).
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("HikariCP connection pool shut down.");
        }
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /**
     * Returns the singleton {@link HikariDataSource}, creating it on first use.
     */
    private static HikariDataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (LOCK) {
                if (dataSource == null || dataSource.isClosed()) {
                    dataSource = buildDataSource();
                }
            }
        }
        return dataSource;
    }

    /**
     * Constructs and configures a new {@link HikariDataSource} from environment
     * variables and (optionally) AWS Secrets Manager.
     *
     * <p>Explicit connection, socket, and pool-level timeouts are configured to
     * prevent indefinite hangs in cloud environments with variable network
     * latency or transient service failures (cr-java-0097).</p>
     */
    private static HikariDataSource buildDataSource() {

        // --- Connection coordinates -------------------------------------------
        String host   = getEnv("DB_HOST", "localhost");
        String port   = getEnv("DB_PORT", "3306");
        String dbName = getEnv("DB_NAME", "hospital");

        // Resolve timeout values from environment so they are tunable per environment.
        long connectionTimeoutMs = getEnvLong("DB_POOL_CONNECTION_TIMEOUT", 30_000L);
        long socketTimeoutMs     = getEnvLong("DB_SOCKET_TIMEOUT_MS", 30_000L);

        // Convert ms → seconds for MySQL JDBC URL parameters (connectTimeout / socketTimeout
        // are expressed in milliseconds for MySQL Connector/J 8.x).
        // Socket-level timeout prevents indefinite hangs when the network drops mid-query.
        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s"
                + "?useSSL=true&requireSSL=false"
                + "&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8"
                + "&connectTimeout=%d"    // TCP connect timeout (ms) – cr-java-0097
                + "&socketTimeout=%d",    // Socket read/write timeout (ms) – cr-java-0097
                host, port, dbName,
                connectionTimeoutMs,
                socketTimeoutMs);

        // --- Credentials (AWS Secrets Manager preferred) ----------------------
        String username;
        String password;

        String secretName = System.getenv("DB_SECRET_NAME");
        if (secretName != null && !secretName.isEmpty()) {
            LOGGER.info("Retrieving DB credentials from AWS Secrets Manager secret: " + secretName);
            username = AwsSecretsManagerUtil.getSecretValue(secretName, "username");
            password = AwsSecretsManagerUtil.getSecretValue(secretName, "password");
        } else {
            LOGGER.warning("DB_SECRET_NAME not set – falling back to DB_USERNAME / DB_PASSWORD env vars.");
            username = getEnv("DB_USERNAME", "root");
            password = getEnv("DB_PASSWORD", "");
        }

        // --- HikariCP configuration -------------------------------------------
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Pool sizing
        config.setMaximumPoolSize(
                getEnvInt("DB_POOL_MAX_SIZE", 10));
        config.setMinimumIdle(
                getEnvInt("DB_POOL_MIN_IDLE", 2));

        // --- Explicit timeout configuration (cr-java-0097) --------------------
        // connectionTimeout: max ms to wait for a connection from the pool before
        //   throwing an exception – prevents indefinite blocking.
        config.setConnectionTimeout(connectionTimeoutMs);

        // idleTimeout: max ms a connection may sit idle in the pool before being
        //   retired – reclaims resources when load drops.
        config.setIdleTimeout(
                getEnvLong("DB_POOL_IDLE_TIMEOUT", 600_000L));

        // maxLifetime: max ms a connection may live in the pool regardless of
        //   activity – forces periodic recycling to avoid stale connections.
        config.setMaxLifetime(
                getEnvLong("DB_POOL_MAX_LIFETIME", 1_800_000L));

        // initializationFailTimeout: max ms to wait for the pool to be seeded
        //   with at least one valid connection on startup.  A value > 0 causes
        //   HikariCP to fail fast rather than hang indefinitely.
        config.setInitializationFailTimeout(
                getEnvLong("DB_POOL_INIT_FAIL_TIMEOUT", 30_000L));

        // validationTimeout: max ms HikariCP will wait when testing a connection
        //   for liveness before considering it invalid.
        config.setValidationTimeout(5_000L);

        // Health / keep-alive
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("HMS-HikariPool");

        // RDS Proxy: auto-commit enabled by default; adjust per transaction requirements
        config.setAutoCommit(true);

        LOGGER.info(String.format(
                "Initialising HikariCP pool – jdbcUrl=%s, maxPoolSize=%d, "
                + "connectionTimeoutMs=%d, socketTimeoutMs=%d",
                jdbcUrl, config.getMaximumPoolSize(), connectionTimeoutMs, socketTimeoutMs));

        return new HikariDataSource(config);
    }

    // -----------------------------------------------------------------------
    // Utility helpers
    // -----------------------------------------------------------------------

    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private static int getEnvInt(String name, int defaultValue) {
        try {
            String value = System.getenv(name);
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid integer value for env var " + name + "; using default " + defaultValue);
            return defaultValue;
        }
    }

    private static long getEnvLong(String name, long defaultValue) {
        try {
            String value = System.getenv(name);
            return (value != null && !value.isEmpty()) ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid long value for env var " + name + "; using default " + defaultValue);
            return defaultValue;
        }
    }
}
