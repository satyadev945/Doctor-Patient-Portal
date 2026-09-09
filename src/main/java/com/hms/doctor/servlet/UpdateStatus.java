package com.hms.doctor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hms.dao.AppointmentDAO;
import com.hms.db.DBConnection;

/**
 * UpdateStatus – handles doctor appointment comment/status updates.
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
@WebServlet("/updateStatus")
public class UpdateStatus extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {

			int    id       = Integer.parseInt(req.getParameter("id"));
			int    doctorId = Integer.parseInt(req.getParameter("doctorId"));
			String comment  = req.getParameter("comment");

			AppointmentDAO appDAO = new AppointmentDAO(DBConnection.getConn());
			boolean f = appDAO.updateDrAppointmentCommentStatus(id, doctorId, comment);

			// Obtain the Redis-backed distributed session via Spring Session.
			// The springSessionRepositoryFilter (web.xml) intercepts this call and
			// returns a session backed by Amazon ElastiCache for Redis instead of
			// the default in-process container session.
			HttpSession session = req.getSession();

			if (f == true) {
				// Store success flash message in the Redis-backed distributed session.
				session.setAttribute("successMsg", "Comment updated");
				resp.sendRedirect("doctor/patient.jsp");

			} else {
				// Store error flash message in the Redis-backed distributed session.
				session.setAttribute("errorMsg", "Something went wrong on server!");
				resp.sendRedirect("doctor/patient.jsp");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
