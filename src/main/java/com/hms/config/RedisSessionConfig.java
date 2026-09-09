package com.hms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Spring Session Redis configuration for Amazon ElastiCache.
 *
 * Replaces in-process HTTP session storage with a distributed Redis-backed
 * session store, enabling stateless application instances and horizontal
 * scaling without server affinity.
 *
 * Required environment variables:
 *   REDIS_HOST  – ElastiCache primary endpoint (default: localhost)
 *   REDIS_PORT  – ElastiCache port             (default: 6379)
 *
 * The @EnableRedisHttpSession annotation registers the
 * springSessionRepositoryFilter bean that transparently wraps every
 * HttpSession with a Redis-backed implementation, so all existing
 * session.setAttribute / session.getAttribute calls continue to work
 * without any changes to business logic.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class RedisSessionConfig {

    @Value("${REDIS_HOST:localhost}")
    private String redisHost;

    @Value("${REDIS_PORT:6379}")
    private int redisPort;

    /**
     * Lettuce connection factory pointing at the Amazon ElastiCache Redis
     * endpoint.  Host and port are resolved from environment variables so
     * that no credentials or topology details are hard-coded in source.
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisConfig =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(redisConfig);
    }
}
