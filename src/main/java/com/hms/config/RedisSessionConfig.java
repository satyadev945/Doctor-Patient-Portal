package com.hms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * RedisSessionConfig – cloud-readiness fix cr-java-0065
 *
 * Configures Spring Session to store all HTTP session data in Amazon
 * ElastiCache for Redis instead of in-process JVM memory.  This makes every
 * application instance fully stateless and allows horizontal scaling without
 * sticky sessions.
 *
 * Connection parameters are read from environment variables so that no
 * credentials or host names are hard-coded in source code:
 *
 *   REDIS_HOST     – ElastiCache primary endpoint  (default: localhost)
 *   REDIS_PORT     – Redis port                    (default: 6379)
 *   REDIS_PASSWORD – Auth token / password         (default: empty)
 *
 * The DelegatingFilterProxy named "springSessionRepositoryFilter" must be
 * registered in web.xml (see WEB-INF/web.xml) so that every incoming request
 * passes through Spring Session before the servlet container processes it.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class RedisSessionConfig {

    /** ElastiCache primary endpoint – injected from REDIS_HOST env var. */
    @Value("${REDIS_HOST:localhost}")
    private String redisHost;

    /** Redis port – injected from REDIS_PORT env var. */
    @Value("${REDIS_PORT:6379}")
    private int redisPort;

    /** Redis auth token – injected from REDIS_PASSWORD env var. */
    @Value("${REDIS_PASSWORD:}")
    private String redisPassword;

    /**
     * Creates a Lettuce-backed Redis connection factory pointing at the
     * ElastiCache cluster defined by the environment variables above.
     *
     * @return configured {@link LettuceConnectionFactory}
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.setPassword(redisPassword);
        }
        return new LettuceConnectionFactory(config);
    }
}
