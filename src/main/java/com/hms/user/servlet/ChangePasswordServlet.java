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

/**
 * ChangePasswordServlet – handles user password change requests.
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
@WebServlet("/userChangePassword")
public class ChangePasswordServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int    userId      = Integer.parseInt(req.getParameter("userId"));
		String oldPassword = req.getParameter("oldPassword");
		String newPassword = req.getParameter("newPassword");

		UserDAO uDAO = new UserDAO(DBConnection.getConn());

		// Obtain the Redis-backed distributed session via Spring Session.
		// The springSessionRepositoryFilter (web.xml) intercepts this call and
		// returns a session backed by Amazon ElastiCache for Redis instead of
		// the default in-process container session.
		HttpSession session = req.getSession();

		if (uDAO.checkOldPassword(userId, oldPassword)) {

			if (uDAO.changePassword(userId, newPassword)) {
				// Store success flash message in the Redis-backed distributed session.
				session.setAttribute("successMsg", "Password Change Successfully.");
				resp.sendRedirect("change_password.jsp");

			} else {
				// Store error flash message in the Redis-backed distributed session.
				session.setAttribute("errorMsg", "Something wrong on server!");
				resp.sendRedirect("change_password.jsp");
			}

		} else {
			// Store error flash message in the Redis-backed distributed session.
			session.setAttribute("errorMsg", "Old password incorrect");
			resp.sendRedirect("change_password.jsp");
		}
	}

}
