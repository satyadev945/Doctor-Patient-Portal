package com.hms.doctor.servlet;

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

@WebServlet("/doctorLogout")
public class DoctorLogoutServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// cr-java-0065: req.getSession() returns a Spring Session RedisSession
		// backed by Amazon ElastiCache for Redis.
		HttpSession session = req.getSession();
		// cr-java-0065: Attribute removal and success message are propagated to
		// the distributed Redis session store, ensuring all instances see the
		// invalidated state immediately.
		session.removeAttribute("doctorObj");
		session.setAttribute("successMsg", "Doctor Logout Successfully.");
		resp.sendRedirect("doctor_login.jsp");
	}
	
	

}
