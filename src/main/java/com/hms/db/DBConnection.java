package com.hms.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Cloud-readiness fix (cr-java-0073 – Direct JDBC Connections):
 *
 * Replaces raw {@link java.sql.DriverManager#getConnection} with a
 * HikariCP connection pool backed by Amazon RDS Proxy.
 *
 * <p>All connection parameters are resolved from environment variables so that
 * no credentials or host names are hard-coded in source code (12-factor app,
 * factor III – Config):
 *
 * <ul>
 *   <li>{@code DB_HOST}     – RDS Proxy endpoint (or RDS instance endpoint).
 *                             Defaults to {@code localhost} for local dev.</li>
 *   <li>{@code DB_PORT}     – Database port. Defaults to {@code 3306}.</li>
 *   <li>{@code DB_NAME}     – Schema / database name. Defaults to {@code hospital}.</li>
 *   <li>{@code DB_USER}     – Database user. Defaults to {@code root}.</li>
 *   <li>{@code DB_PASSWORD} – Database password. Defaults to empty string.</li>
 * </ul>
 *
 * <p>Cloud-readiness fix (cr-java-0097 – Missing Connection Timeouts):
 *
 * <p>Explicit connection, socket, and validation timeouts are configured at
 * both the JDBC driver level (MySQL {@code connectTimeout} / {@code socketTimeout}
 * URL parameters) and the HikariCP pool level to prevent connections from
 * hanging indefinitely in cloud environments with variable network latency or
 * transient service failures:
 *
 * <ul>
 *   <li>JDBC {@code connectTimeout}  : 10 s – max time to establish a TCP connection to the DB server.</li>
 *   <li>JDBC {@code socketTimeout}   : 30 s – max time to wait for data on an established socket.</li>
 *   <li>HikariCP {@code connectionTimeout} : 30 s – max wait for a connection from the pool.</li>
 *   <li>HikariCP {@code validationTimeout} : 5 s  – max time to validate a connection before use.</li>
 *   <li>HikariCP {@code idleTimeout}       : 600 s – idle connection eviction interval.</li>
 *   <li>HikariCP {@code keepaliveTime}     : 300 s – periodic keepalive ping to prevent stale connections.</li>
 *   <li>HikariCP {@code maxLifetime}       : 1800 s – maximum connection lifetime.</li>
 * </ul>
 *
 * <p>HikariCP pool settings are intentionally conservative so that the
 * application works well behind Amazon RDS Proxy, which itself manages a
 * larger connection pool toward the underlying RDS instance:
 * <ul>
 *   <li>Maximum pool size : 10 connections</li>
 *   <li>Minimum idle      : 2 connections</li>
 * </ul>
 */
public class DBConnection {

    // -----------------------------------------------------------------------
    // Environment-variable names
    // -----------------------------------------------------------------------
    private static final String ENV_DB_HOST     = "DB_HOST";
    private static final String ENV_DB_PORT     = "DB_PORT";
    private static final String ENV_DB_NAME     = "DB_NAME";
    private static final String ENV_DB_USER     = "DB_USER";
    private static final String ENV_DB_PASSWORD = "DB_PASSWORD";

    // -----------------------------------------------------------------------
    // Defaults (used only when the corresponding env-var is absent)
    // -----------------------------------------------------------------------
    private static final String DEFAULT_HOST     = "localhost";
    private static final String DEFAULT_PORT     = "3306";
    private static final String DEFAULT_DB_NAME  = "hospital";
    private static final String DEFAULT_USER     = "root";
    private static final String DEFAULT_PASSWORD = "";

    // -----------------------------------------------------------------------
    // Timeout constants (milliseconds unless noted)
    // cr-java-0097 – Missing Connection Timeouts
    // -----------------------------------------------------------------------
    /** MySQL driver-level TCP connect timeout (ms). Prevents indefinite hangs
     *  when the DB host is unreachable in a cloud VPC. */
    private static final int JDBC_CONNECT_TIMEOUT_MS  = 10_000;  // 10 s

    /** MySQL driver-level socket read/write timeout (ms). Prevents a query
     *  from blocking forever if the network drops mid-flight. */
    private static final int JDBC_SOCKET_TIMEOUT_MS   = 30_000;  // 30 s

    /** HikariCP: max time (ms) a caller waits for a connection from the pool. */
    private static final long HIKARI_CONNECTION_TIMEOUT_MS = 30_000L;  // 30 s

    /** HikariCP: max time (ms) to validate a connection before handing it out. */
    private static final long HIKARI_VALIDATION_TIMEOUT_MS =  5_000L;  //  5 s

    /** HikariCP: how long (ms) a connection may sit idle before being evicted. */
    private static final long HIKARI_IDLE_TIMEOUT_MS       = 600_000L; // 10 min

    /** HikariCP: periodic keepalive ping interval (ms) to prevent stale
     *  connections behind AWS NAT gateways / RDS Proxy idle timeouts. */
    private static final long HIKARI_KEEPALIVE_TIME_MS     = 300_000L; //  5 min

    /** HikariCP: maximum lifetime (ms) of a connection in the pool. */
    private static final long HIKARI_MAX_LIFETIME_MS       = 1_800_000L; // 30 min

    // -----------------------------------------------------------------------
    // Singleton HikariCP data source – initialised once on first use
    // -----------------------------------------------------------------------
    private static volatile HikariDataSource dataSource;

    /** Utility class – prevent instantiation. */
    private DBConnection() {}

    /**
     * Returns a {@link Connection} obtained from the HikariCP pool.
     *
     * <p>Callers are responsible for closing the connection (preferably via
     * try-with-resources) so that it is returned to the pool.
     *
     * @return a pooled {@link Connection}
     * @throws RuntimeException if a connection cannot be obtained
     */
    public static Connection getConn() {
        if (dataSource == null) {
            synchronized (DBConnection.class) {
                if (dataSource == null) {
                    dataSource = buildDataSource();
                }
            }
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to obtain a database connection from HikariCP pool", e);
        }
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /**
     * Builds and configures the {@link HikariDataSource}.
     *
     * <p>The JDBC URL targets Amazon RDS Proxy (or a plain RDS endpoint) whose
     * host is supplied via the {@code DB_HOST} environment variable.
     *
     * <p>cr-java-0097: Both JDBC driver-level timeouts ({@code connectTimeout},
     * {@code socketTimeout}) and HikariCP pool-level timeouts
     * ({@code connectionTimeout}, {@code validationTimeout},
     * {@code keepaliveTime}) are explicitly set to prevent indefinite hangs
     * in cloud environments.
     */
    private static HikariDataSource buildDataSource() {
        String host     = getEnv(ENV_DB_HOST,     DEFAULT_HOST);
        String port     = getEnv(ENV_DB_PORT,     DEFAULT_PORT);
        String dbName   = getEnv(ENV_DB_NAME,     DEFAULT_DB_NAME);
        String user     = getEnv(ENV_DB_USER,     DEFAULT_USER);
        String password = getEnv(ENV_DB_PASSWORD, DEFAULT_PASSWORD);

        // Build JDBC URL pointing at RDS Proxy / RDS endpoint.
        // cr-java-0097: connectTimeout and socketTimeout are added as JDBC URL
        // parameters so that the MySQL Connector/J driver enforces network-level
        // timeouts independently of the HikariCP pool-level timeout.
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useSSL=true&requireSSL=false&serverTimezone=UTC"
                + "&useUnicode=true&characterEncoding=UTF-8"
                + "&connectTimeout=" + JDBC_CONNECT_TIMEOUT_MS   // TCP connect timeout (ms)
                + "&socketTimeout="  + JDBC_SOCKET_TIMEOUT_MS;   // Socket read/write timeout (ms)

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Pool sizing – kept small because RDS Proxy multiplexes connections
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        // ----------------------------------------------------------------
        // cr-java-0097 – Explicit HikariCP timeout configuration
        // Prevents connections from hanging indefinitely in cloud
        // environments with variable network latency or service failures.
        // ----------------------------------------------------------------

        // Max time (ms) a caller waits to acquire a connection from the pool.
        // Throws SQLException after this period instead of blocking forever.
        config.setConnectionTimeout(HIKARI_CONNECTION_TIMEOUT_MS);

        // Max time (ms) HikariCP will wait to validate a connection before
        // handing it to the caller. Must be less than connectionTimeout.
        config.setValidationTimeout(HIKARI_VALIDATION_TIMEOUT_MS);

        // How long (ms) a connection may sit idle before being evicted.
        config.setIdleTimeout(HIKARI_IDLE_TIMEOUT_MS);

        // Periodic keepalive ping interval (ms). Prevents AWS NAT gateway /
        // RDS Proxy from silently dropping idle connections.
        config.setKeepaliveTime(HIKARI_KEEPALIVE_TIME_MS);

        // Maximum lifetime (ms) of a connection in the pool.
        config.setMaxLifetime(HIKARI_MAX_LIFETIME_MS);

        // Pool name for JMX / logging
        config.setPoolName("HmsHikariPool");

        // Validation query for MySQL
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }

    /**
     * Reads an environment variable, returning {@code defaultValue} when the
     * variable is absent or blank.
     */
    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : defaultValue;
    }
}
