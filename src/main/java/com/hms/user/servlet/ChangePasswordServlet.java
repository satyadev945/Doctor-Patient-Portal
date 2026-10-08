package com.hms.user.servlet;

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

import com.hms.dao.UserDAO;
import com.hms.db.DBConnection;

@WebServlet("/userChangePassword")
public class ChangePasswordServlet extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		int userId = Integer.parseInt(req.getParameter("userId"));
		String oldPassword = req.getParameter("oldPassword");
		String newPassword = req.getParameter("newPassword");
		
		UserDAO uDAO = new UserDAO(DBConnection.getConn());
		//boolean f = uDAO.checkOldPassword(userId, oldPassword);
		
		// cr-java-0065: req.getSession() returns a Spring Session RedisSession
		// backed by Amazon ElastiCache for Redis.
		HttpSession session = req.getSession();
		
		if(uDAO.checkOldPassword(userId, oldPassword)) {
			
			if(uDAO.changePassword(userId, newPassword)) {
				// cr-java-0065: Success message stored in distributed Redis session –
				// available across all application instances.
				session.setAttribute("successMsg", "Password Change Successfully.");
				resp.sendRedirect("change_password.jsp");
				
			}else {
				// cr-java-0065: Error message stored in distributed Redis session.
				session.setAttribute("errorMsg", "Something wrong on server!");
				resp.sendRedirect("change_password.jsp");
				
			}
			
		}else {
			// cr-java-0065: Error message stored in distributed Redis session.
			session.setAttribute("errorMsg", "Old password incorrect");
			resp.sendRedirect("change_password.jsp");
		}
		
		
		
	}
	
	

}
