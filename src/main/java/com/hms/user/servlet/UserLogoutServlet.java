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

/**
 * User logout servlet.
 *
 * cr-java-0065 – HTTP Session State Storage remediation:
 * Session state is stored in Amazon ElastiCache for Redis via Spring Session.
 * The {@code HttpSession} API is unchanged; the Spring Session filter
 * (registered in web.xml as "springSessionRepositoryFilter") transparently
 * replaces the in-memory session store with a distributed Redis-backed store,
 * enabling stateless application instances and horizontal scaling on AWS
 * without sticky-session load-balancer affinity.
 */
@WebServlet("/userLogout")
public class UserLogoutServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// cr-java-0065: req.getSession() returns a Spring Session RedisSession
		// backed by Amazon ElastiCache for Redis (configured in RedisSessionConfig).
		// Session data is stored in the distributed Redis store, not in JVM memory.
		HttpSession session = req.getSession();

		// cr-java-0065: Attribute removal is propagated to the distributed Redis
		// session store via Spring Session, ensuring all instances see the
		// invalidated state immediately — no server affinity required.
		session.removeAttribute("userObj");

		// cr-java-0065: Success message is stored in the Redis-backed session,
		// visible to any application instance handling the next request.
		session.setAttribute("successMsg", "User Logout Successfully.");

		resp.sendRedirect("user_login.jsp");
	}

}
