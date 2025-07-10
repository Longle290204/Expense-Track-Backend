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

import com.example.expensetrackapp.auth.models.LoginRequest;
import com.example.expensetrackapp.auth.models.LoginResponse;
import com.example.expensetrackapp.auth.models.RegisterRequest;
import com.example.expensetrackapp.auth.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
	private AuthService authService;
	private ObjectMapper objectMapper;

	@Override
	public void init() throws ServletException {
		super.init();
		authService = new AuthService();
		objectMapper = new ObjectMapper();
		logger.info("AuthServlet initialized successfully");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		String pathInfor = request.getPathInfo();

		if (pathInfor == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("{\"success\": false, \"message\": \"Missing API endpoint.\"}");
			return;
		}

		switch (pathInfor) {
		case "/register":
			handleRegister(request, response, out);
			break;
		case "/login":
			handleLogin(request, response, out);
			break;
		default:
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			out.print("{\"success\": false, \"message\": \"API endpoint not found.\"}");
			break;
		}

	}

	public void handleRegister(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {
		try {
			// Map data from http body
			RegisterRequest registerRequest = objectMapper.readValue(request.getReader(), RegisterRequest.class);

			if (registerRequest.getUsername() == null || registerRequest.getPassword() == null
					|| registerRequest.getEmail() == null) {
				out.print("{\"success\": false, \"message\": \"Username and password are required.\"}");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			if (authService.registerUser(registerRequest.getUsername(), registerRequest.getPassword(),
					registerRequest.getEmail())) {
				response.setStatus(HttpServletResponse.SC_CREATED);
				out.print("{\"success\": true, \"message\": \"Register is successfully\".}");
			} else {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				out.print("{\"success\": false, \"message\": \"Username already exists or registration failed\".}");
			}

		} catch (Exception e) {
			logger.error("Error during registration", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
			out.print("{\"success\": false, \"message\": \"An error occurred during registration.\"}");
		}
	}

	public void handleLogin(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {
		try { // Thêm try-catch để bắt lỗi đọc JSON hoặc các lỗi khác
			LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);

			String username = loginRequest.getUsername();
			String password = loginRequest.getPassword();

			if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("{\"success\": false, \"message\": \"Username and password are required.\"}");
				return;
			}

			String jwtToken = authService.loginUser(username, password);

			if (jwtToken != null) {
				LoginResponse loginResponse = new LoginResponse();
				loginResponse.setUsername(username);
				loginResponse.setToken(jwtToken);
				loginResponse.setSuccess(true);
				loginResponse.setMessage("Login successfully!"); // Sửa chính tả "succesfully" -> "successfully"

				response.setStatus(HttpServletResponse.SC_OK); // 200 OK cho đăng nhập thành công
				// Chuyển đổi đối tượng loginResponse thành JSON và gửi về client
				out.print(objectMapper.writeValueAsString(loginResponse));
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
				out.print("{\"success\": false, \"message\": \"Invalid credentials.\"}");
			}
		} catch (Exception e) { // Bắt các Exception khác có thể xảy ra trong quá trình xử lý
			logger.error("Error during login", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
			out.print("{\"success\": false, \"message\": \"An error occurred during login.\"}");
		}
	}
}