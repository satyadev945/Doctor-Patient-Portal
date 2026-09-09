package com.hms.admin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * AdminLogoutServlet – handles admin logout.
 *
 * Session invalidation is performed via the standard HttpSession API.
 * The springSessionRepositoryFilter registered in web.xml transparently
 * delegates to Amazon ElastiCache for Redis (Spring Session), so
 * session.removeAttribute / session.setAttribute operate on the
 * distributed Redis store rather than in-process memory (cr-java-0065).
 */
@WebServlet("/adminLogout")
public class AdminLogoutServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// HttpSession is backed by Amazon ElastiCache for Redis via Spring Session.
		// Removing the adminObj attribute invalidates the admin's distributed session,
		// ensuring logout is effective across all application instances.
		HttpSession session = req.getSession();
		session.removeAttribute("adminObj");
		//show message after logout
		session.setAttribute("successMsg", "Admin Logout Successfully");
		resp.sendRedirect("admin_login.jsp");
		
		
		
	}

	
}
