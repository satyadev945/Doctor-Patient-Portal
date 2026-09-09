package com.hms.doctor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hms.dao.DoctorDAO;
import com.hms.db.DBConnection;
import com.hms.entity.Doctor;

/**
 * DoctorEditProfileServlet – handles doctor profile edit requests.
 *
 * HTTP Session State (cr-java-0065):
 * ------------------------------------
 * All HTTP session state is managed via Amazon ElastiCache for Redis using
 * Spring Session. The springSessionRepositoryFilter registered in web.xml
 * transparently replaces the standard in-process HttpSession with a
 * Redis-backed distributed session, so session attributes (successMsgForD /
 * errorMsgForD / doctorObj) are stored in the centralized Redis store rather
 * than in-process memory. This enables stateless application instances and
 * horizontal scaling without server affinity or sticky sessions.
 *
 * Required environment variables (consumed by RedisSessionConfig):
 *   REDIS_HOST – ElastiCache primary endpoint (default: localhost)
 *   REDIS_PORT – ElastiCache port             (default: 6379)
 */
@WebServlet("/doctorEditProfile")
public class DoctorEditProfileServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {

			// get all data which is coming from doctor.jsp doctor details
			String fullName = req.getParameter("fullName");
			String dateOfBirth = req.getParameter("dateOfBirth");
			String qualification = req.getParameter("qualification");
			String specialist = req.getParameter("specialist");
			String email = req.getParameter("email");
			String phone = req.getParameter("phone");
			//String password = req.getParameter("password");

			int id = Integer.parseInt(req.getParameter("doctorId"));

			Doctor doctor = new Doctor(id, fullName, dateOfBirth, qualification, specialist, email, phone, "");

			DoctorDAO docDAO = new DoctorDAO(DBConnection.getConn());

			boolean f = docDAO.editDoctorProfile(doctor);

			// Obtain the Redis-backed distributed session via Spring Session.
			// The springSessionRepositoryFilter (web.xml) intercepts this call and
			// returns a session backed by Amazon ElastiCache for Redis instead of
			// the default in-process container session. Session attributes written
			// here are stored in the centralized Redis store and are available to
			// all load-balanced instances without requiring sticky sessions.
			HttpSession session = req.getSession();

			if (f == true) {
				Doctor updateDoctorObj = docDAO.getDoctorById(id);
				// Store success flash message in the Redis-backed distributed session
				session.setAttribute("successMsgForD", "Doctor update Successfully");
				// Store updated doctor object in the Redis-backed distributed session;
				// overrides the previous session value with the newly updated doctor.
				session.setAttribute("doctorObj", updateDoctorObj);
				resp.sendRedirect("doctor/edit_profile.jsp");

			} else {
				// Store error flash message in the Redis-backed distributed session
				session.setAttribute("errorMsgForD", "Something went wrong on server!");
				resp.sendRedirect("doctor/edit_profile.jsp");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
