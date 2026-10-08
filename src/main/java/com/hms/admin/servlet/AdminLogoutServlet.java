package com.hms.admin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// cr-java-0065: HttpSession is now backed by Amazon ElastiCache for Redis via
// Spring Session (RedisSessionConfig + springSessionRepositoryFilter in web.xml).
// The HttpSession API is unchanged; Spring Session transparently stores all
// session attributes in the distributed Redis store, enabling stateless,
// horizontally scalable instances without sticky-session load-balancer affinity.
import javax.servlet.http.HttpSession;

@WebServlet("/adminLogout")
public class AdminLogoutServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// cr-java-0065: req.getSession() returns a Spring Session RedisSession.
		// removeAttribute and setAttribute operate on the distributed Redis store.
		HttpSession session = req.getSession();
		// get session means get "adminObj" and remove it, logout done!
		session.removeAttribute("adminObj");
		//show message after logout
		// cr-java-0065: Success message stored in distributed Redis session.
		session.setAttribute("successMsg", "Admin Logout Successfully");
		resp.sendRedirect("admin_login.jsp");
		
		
		
	}

	
}
