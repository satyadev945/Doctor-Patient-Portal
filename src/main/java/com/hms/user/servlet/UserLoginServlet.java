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
 * UserLoginServlet – handles user authentication and session establishment.
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
 */
@WebServlet("/userLogin")
public class UserLoginServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String email    = req.getParameter("email");
		String password = req.getParameter("password");

		// Obtain the Redis-backed distributed session via Spring Session.
		// The springSessionRepositoryFilter (web.xml) intercepts this call and
		// returns a session backed by Amazon ElastiCache for Redis instead of
		// the default in-process container session.
		HttpSession session = req.getSession();

		UserDAO userDAO = new UserDAO(DBConnection.getConn());
		User user = userDAO.loginUser(email, password);

		if (user != null) {
			// Store authenticated user object in the Redis-backed distributed session.
			session.setAttribute("userObj", user);
			resp.sendRedirect("index.jsp");
		} else {
			// Store error flash message in the Redis-backed distributed session.
			session.setAttribute("errorMsg", "Invalid email or password");
			resp.sendRedirect("user_login.jsp");
		}
	}

}
