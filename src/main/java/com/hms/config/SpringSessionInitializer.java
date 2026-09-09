package com.hms.config;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * SpringSessionInitializer – Registers the Spring Session filter with the
 * servlet container programmatically.
 *
 * Extending AbstractHttpSessionApplicationInitializer ensures that the
 * springSessionRepositoryFilter is registered before any other filter in the
 * web application. This filter intercepts every HTTP request and replaces the
 * default container-managed HttpSession with a Redis-backed session provided
 * by Spring Session + Amazon ElastiCache for Redis (cr-java-0065).
 *
 * This class complements the web.xml DelegatingFilterProxy declaration and
 * ensures the Spring application context (RedisSessionConfig) is loaded
 * before any session-dependent servlet or filter is initialised.
 */
public class SpringSessionInitializer extends AbstractHttpSessionApplicationInitializer {

    public SpringSessionInitializer() {
        super(RedisSessionConfig.class);
    }
}
