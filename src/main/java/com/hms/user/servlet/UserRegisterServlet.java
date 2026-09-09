package com.hms.user.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hms.dao.UserDAO;
import com.hms.db.DBConnection;
import com.hms.entity.User;

/**
 * UserRegisterServlet – handles new user registration.
 *
 * HTTP Session State (cr-java-0065):
 * ------------------------------------
 * All HTTP session state is managed via Amazon ElastiCache for Redis using
 * Spring Session. The springSessionRepositoryFilter registered in web.xml
 * transparently replaces the standard in-process HttpSession with a
 * Redis-backed distributed session. When session.setAttribute() is called
 * here, the operations are applied to the centralized Redis session store,
 * ensuring consistent state across all load-balanced application instances
 * without server affinity.
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
@WebServlet("/user_register")
public class UserRegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            // get all data/value which is coming from signup.jsp page for new User
            // registration
            String fullName = req.getParameter("fullName");
            String email    = req.getParameter("email");
            String password = req.getParameter("password");

            // Set all data to User Entity
            User user = new User(fullName, email, password);

            // Create Connection with DB
            UserDAO userDAO = new UserDAO(DBConnection.getConn());

            // Obtain the Redis-backed distributed session via Spring Session.
            // The springSessionRepositoryFilter (web.xml) intercepts this call and
            // returns a session backed by Amazon ElastiCache for Redis instead of
            // the default in-process container session. This ensures session state
            // is shared across all horizontally-scaled application instances.
            HttpSession session = req.getSession();

            // call userRegister() and pass user object to insert or save user into DB.
            boolean f = userDAO.userRegister(user); // userRegister() method return boolean type value

            if (f) {
                // Store success flash message in the Redis-backed distributed session
                // so it is available after the redirect regardless of which instance
                // handles the subsequent request.
                session.setAttribute("successMsg", "Register Successfully");
                resp.sendRedirect("signup.jsp"); // which page you want to show this msg

            } else {
                // Store error flash message in the Redis-backed distributed session.
                session.setAttribute("errorMsg", "Something went wrong!");
                resp.sendRedirect("signup.jsp"); // which page you want to show this msg
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
