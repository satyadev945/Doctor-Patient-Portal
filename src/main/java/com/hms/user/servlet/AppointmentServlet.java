package com.hms.user.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hms.dao.AppointmentDAO;
import com.hms.db.DBConnection;
import com.hms.entity.Appointment;

/**
 * AppointmentServlet – handles patient appointment booking.
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
@WebServlet("/addAppointment")
public class AppointmentServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int    userId          = Integer.parseInt(req.getParameter("userId"));
		String fullName        = req.getParameter("fullName");
		String gender          = req.getParameter("gender");
		String age             = req.getParameter("age");
		String appointmentDate = req.getParameter("appointmentDate");
		String email           = req.getParameter("email");
		String phone           = req.getParameter("phone");
		String diseases        = req.getParameter("diseases");
		int    doctorId        = Integer.parseInt(req.getParameter("doctorNameSelect"));
		String address         = req.getParameter("address");

		Appointment appointment = new Appointment(userId, fullName, gender, age, appointmentDate, email, phone, diseases, doctorId, address, "Pending");

		AppointmentDAO appointmentDAO = new AppointmentDAO(DBConnection.getConn());
		boolean f = appointmentDAO.addAppointment(appointment);

		// Obtain the Redis-backed distributed session via Spring Session.
		// The springSessionRepositoryFilter (web.xml) intercepts this call and
		// returns a session backed by Amazon ElastiCache for Redis instead of
		// the default in-process container session.
		HttpSession session = req.getSession();

		if (f == true) {
			// Store success flash message in the Redis-backed distributed session.
			session.setAttribute("successMsg", "Appointment is recorded Successfully.");
			resp.sendRedirect("user_appointment.jsp");

		} else {
			// Store error flash message in the Redis-backed distributed session.
			session.setAttribute("errorMsg", "Something went wrong on server!");
			resp.sendRedirect("user_appointment.jsp");
		}
	}

}
