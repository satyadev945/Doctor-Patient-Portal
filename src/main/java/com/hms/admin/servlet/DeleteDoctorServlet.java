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

import com.hms.dao.DoctorDAO;
import com.hms.db.DBConnection;

@WebServlet("/deleteDoctor")
public class DeleteDoctorServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//get id(which is coming as string value) and convert into int	
		int id = Integer.parseInt(req.getParameter("id"));
		
		DoctorDAO docDAO = new DoctorDAO(DBConnection.getConn());
		// cr-java-0065: req.getSession() returns a Spring Session RedisSession
		// backed by Amazon ElastiCache for Redis.
		HttpSession session = req.getSession();
		
		boolean f = docDAO.deleteDoctorById(id);
		
		if(f==true) {
			// cr-java-0065: Success message stored in distributed Redis session.
			session.setAttribute("successMsg", "Doctor Deleted Successfully.");
			resp.sendRedirect("admin/view_doctor.jsp");
		}
		else {
			// cr-java-0065: Error message stored in distributed Redis session.
			session.setAttribute("errorMsg", "Something went wrong on server!");
			resp.sendRedirect("admin/view_doctor.jsp");
		}
	}
	
	

}
