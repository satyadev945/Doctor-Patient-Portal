package com.hms.user.servlet;

import java.io.IOException;
import java.io.PrintWriter;

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
import com.hms.entity.User;

/**
 * User registration servlet.
 *
 * cr-java-0065 – HTTP Session State Storage remediation:
 * All HTTP session state is stored in Amazon ElastiCache for Redis via Spring
 * Session. The {@code HttpSession} API is unchanged; the Spring Session filter
 * (registered in web.xml as "springSessionRepositoryFilter") transparently
 * replaces the in-memory session store with a distributed Redis-backed store,
 * enabling stateless application instances and horizontal scaling on AWS
 * without sticky-session load-balancer affinity.
 */
@WebServlet("/user_register")
public class UserRegisterServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {

			// PrintWriter out = resp.getWriter();

			// get all data/value which is coming from signup.jsp page for new User
			// registration
			String fullName = req.getParameter("fullName");
			String email = req.getParameter("email");
			String password = req.getParameter("password");

			// Set all data to User Entity
			User user = new User(fullName, email, password);

			// Create Connection with DB
			UserDAO userDAO = new UserDAO(DBConnection.getConn());

			// cr-java-0065: req.getSession() returns a Spring Session RedisSession
			// backed by Amazon ElastiCache for Redis (configured in RedisSessionConfig).
			// Session data is stored in the distributed Redis store, not in JVM memory,
			// enabling distributed session state across all horizontally scaled instances.
			HttpSession session = req.getSession();

			// call userRegister() and pass user object to insert or save user into DB.
			boolean f = userDAO.userRegister(user); // userRegister() method return boolean type value

			if (f == true) {

				// cr-java-0065: Session attribute is stored in the distributed Redis
				// session store via Spring Session; all instances share the same state.
				session.setAttribute("successMsg", "Register Successfully");
				resp.sendRedirect("signup.jsp"); // which page you want to show this msg
				// System.out.println("register successfull");
				// out.println("success");

			} else {

				// cr-java-0065: Session attribute is stored in the distributed Redis
				// session store via Spring Session; all instances share the same state.
				session.setAttribute("errorMsg", "Something went wrong!");
				resp.sendRedirect("signup.jsp"); // which page you want to show this msg

				// System.out.println("Error! Something went wrong");
				// out.println("error");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
