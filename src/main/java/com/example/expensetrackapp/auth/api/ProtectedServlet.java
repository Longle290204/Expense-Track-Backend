package com.example.expensetrackapp.auth.api;

import java.io.IOException;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/protected")
public class ProtectedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ProtectedServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		String username = (String) request.getAttribute("username");
		String role = (String) request.getAttribute("role");

		if (username != null) {
			logger.info("Access granted to protected resource for user: {} with role: {}", username, role);
			response.setStatus(HttpServletResponse.SC_OK); // 200 OK
			out.print(String.format(
					"{\"success\": true, \"message\": \"Welcome to protected area, %s! Your role is %s.\"}", username,
					role));
		} else {
			// Trường hợp này không nên xảy ra nếu JwtAuthFilter hoạt động đúng
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
			out.print("{\"success\": false, \"message\": \"Unauthorized access. (No username in request)\"}");
		}
	}
}