package com.hms.doctor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * DoctorLogoutServlet – handles doctor logout and session invalidation.
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
 */
@WebServlet("/doctorLogout")
public class DoctorLogoutServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Obtain the Redis-backed distributed session via Spring Session.
		// The springSessionRepositoryFilter (web.xml) intercepts this call and
		// returns a session backed by Amazon ElastiCache for Redis instead of
		// the default in-process container session.
		HttpSession session = req.getSession();

		// Remove the doctor object from the Redis-backed distributed session,
		// effectively logging the doctor out across all load-balanced instances.
		session.removeAttribute("doctorObj");

		// Store logout success flash message in the Redis-backed distributed session
		session.setAttribute("successMsg", "Doctor Logout Successfully.");

		resp.sendRedirect("doctor_login.jsp");
	}

}
