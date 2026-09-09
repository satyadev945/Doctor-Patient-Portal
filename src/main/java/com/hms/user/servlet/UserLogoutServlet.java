package com.hms.user.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * UserLogoutServlet – handles user logout and session invalidation.
 *
 * HTTP Session State (cr-java-0065):
 * ------------------------------------
 * All HTTP session state is managed via Amazon ElastiCache for Redis using
 * Spring Session. The springSessionRepositoryFilter registered in web.xml
 * transparently replaces the standard in-process HttpSession with a
 * Redis-backed distributed session. When session.removeAttribute() and
 * session.setAttribute() are called here, the operations are applied to the
 * centralized Redis session store, ensuring consistent state across all
 * load-balanced application instances without server affinity.
 *
 * Required environment variables (consumed by RedisSessionConfig):
 *   REDIS_HOST – ElastiCache primary endpoint (default: localhost)
 *   REDIS_PORT – ElastiCache port             (default: 6379)
 *
 * Spring Session Redis integration:
 *   - RedisSessionConfig.java  : @EnableRedisHttpSession + LettuceConnectionFactory bean
 *   - SpringSessionInitializer : registers springSessionRepositoryFilter programmatically
 *   - web.xml                  : DelegatingFilterProxy for springSessionRepositoryFilter
 *   - pom.xml                  : spring-session-data-redis + lettuce-core dependencies
 */
@WebServlet("/userLogout")
public class UserLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Obtain the Redis-backed distributed session via Spring Session.
        // The springSessionRepositoryFilter (web.xml) intercepts this call and
        // returns a session backed by Amazon ElastiCache for Redis instead of
        // the default in-process container session. This ensures session state
        // is shared across all horizontally-scaled application instances.
        HttpSession session = req.getSession(false);

        if (session != null) {
            // Remove the authenticated user object from the Redis-backed
            // distributed session stored in Amazon ElastiCache for Redis.
            session.removeAttribute("userObj");

            // Store logout success flash message in the Redis-backed
            // distributed session so it survives the redirect.
            session.setAttribute("successMsg", "User Logout Successfully.");
        }

        resp.sendRedirect("user_login.jsp");
    }
}
