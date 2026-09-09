package com.hms.admin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hms.dao.SpecialistDAO;
import com.hms.db.DBConnection;

/**
 * SpecialistServlet – handles addition of a new specialist category.
 *
 * HTTP Session State (cr-java-0065):
 * ------------------------------------
 * All HTTP session state is managed via Amazon ElastiCache for Redis using
 * Spring Session. The springSessionRepositoryFilter registered in web.xml
 * transparently replaces the standard in-process HttpSession with a
 * Redis-backed distributed session, so session attributes (successMsg /
 * errorMsg) are stored in the centralized Redis store rather than in-process
 * memory. This enables stateless application instances and horizontal scaling
 * without server affinity or sticky sessions.
 *
 * Required environment variables:
 *   REDIS_HOST – ElastiCache primary endpoint (default: localhost)
 *   REDIS_PORT – ElastiCache port             (default: 6379)
 */
@WebServlet("/addSpecialist")
public class SpecialistServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String specialistName = req.getParameter("specialistName");

		SpecialistDAO specialistDAO = new SpecialistDAO(DBConnection.getConn());
		boolean f = specialistDAO.addSpecialist(specialistName);

		// HttpSession is transparently backed by Amazon ElastiCache for Redis
		// via Spring Session (springSessionRepositoryFilter in web.xml).
		// Flash messages stored here are available to all load-balanced
		// instances without requiring sticky sessions.
		HttpSession session = req.getSession();

		if (f == true) {
			session.setAttribute("successMsg", "Specialist added Successfully.");
			resp.sendRedirect("admin/index.jsp");

		} else {
			session.setAttribute("errorMsg", "Something went wrong on server");
			resp.sendRedirect("admin/index.jsp");
		}
	}

}
