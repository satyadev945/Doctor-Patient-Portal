package com.hms.doctor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hms.dao.DoctorDAO;
import com.hms.dao.UserDAO;
import com.hms.db.DBConnection;
import com.hms.entity.Doctor;

/**
 * DoctorLoginServlet – handles doctor authentication and session creation.
 *
 * HTTP Session State (cr-java-0065):
 * ------------------------------------
 * All HTTP session state is managed via Amazon ElastiCache for Redis using
 * Spring Session. The springSessionRepositoryFilter registered in web.xml
 * transparently replaces the standard in-process HttpSession with a
 * Redis-backed distributed session, so session attributes (doctorObj /
 * errorMsg) are stored in the centralized Redis store rather than in-process
 * memory. This enables stateless application instances and horizontal scaling
 * without server affinity or sticky sessions.
 *
 * Required environment variables (consumed by RedisSessionConfig):
 *   REDIS_HOST – ElastiCache primary endpoint (default: localhost)
 *   REDIS_PORT – ElastiCache port             (default: 6379)
 */
@WebServlet("/doctorLogin")
public class DoctorLoginServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// get email and password which is coming from doctor_login.jsp page
		String email = req.getParameter("email");
		String password = req.getParameter("password");

		// Obtain the Redis-backed distributed session via Spring Session.
		// The springSessionRepositoryFilter (web.xml) intercepts this call and
		// returns a session backed by Amazon ElastiCache for Redis instead of
		// the default in-process container session.
		HttpSession session = req.getSession();

		// create DB connection
		DoctorDAO docDAO = new DoctorDAO(DBConnection.getConn());

		// call loginDoctor() method for doctor login which method declared in DoctorDAO
		Doctor doctor = docDAO.loginDoctor(email, password);

		if (doctor != null) {
			// means doctor is valid or exist
			// Store the authenticated doctor object in the Redis-backed distributed
			// session; available to all load-balanced instances without sticky sessions.
			session.setAttribute("doctorObj", doctor);
			// and redirect the particular doctor index page which is reside doctor folder
			resp.sendRedirect("doctor/index.jsp"); // doctor index means dashboard of doctors
		} else {
			// Store error flash message in the Redis-backed distributed session
			session.setAttribute("errorMsg", "Invalid email or password");
			resp.sendRedirect("doctor_login.jsp");
		}

	}

}
