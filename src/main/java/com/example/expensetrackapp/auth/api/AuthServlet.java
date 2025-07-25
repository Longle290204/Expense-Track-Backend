package com.example.expensetrackapp.auth.api;

import java.io.IOException;


import java.io.PrintWriter;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.expensetrackapp.api.BaseApiServlet;
import com.example.expensetrackapp.auth.models.ExistsRequest;
import com.example.expensetrackapp.auth.models.ExistsResponse;
import com.example.expensetrackapp.auth.models.LoginRequest;
import com.example.expensetrackapp.auth.models.LoginResponse;
import com.example.expensetrackapp.auth.models.RegisterRequest;
import com.example.expensetrackapp.auth.services.AuthService;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/auth/*")
public class AuthServlet extends BaseApiServlet {
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
		case "/checkIfExist":

			handleCheckIfExist(request, response, out);

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
					|| registerRequest.getEmail() == null || registerRequest.getConfirmPassword() == null) {
				out.print("{\"success\": false, \"message\": \"Username and password are required.\"}");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			if (authService.registerUser(registerRequest.getUsername(), registerRequest.getPassword(),
					registerRequest.getEmail(), registerRequest.getConfirmPassword())) {
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

			// Get user-agent & IP
			String userAgent = request.getHeader("User-Agent");
			String ipAddress = request.getRemoteAddr();

			String[] tokens = authService.loginUser(username, password, userAgent, ipAddress);
			String accessToken = tokens[0];
			String refreshToken = tokens[1];

			if (tokens != null) {
				LoginResponse loginResponse = new LoginResponse();
				loginResponse.setUsername(username);
				loginResponse.setAccessToken(accessToken);
				loginResponse.setRefreshToken(refreshToken);
				loginResponse.setSuccess(true);
				loginResponse.setMessage("Login successfully!");

				response.setStatus(HttpServletResponse.SC_OK);
				// Chuyển đổi đối tượng loginResponse thành JSON và gửi về client
				writeJsonResponse(response, loginResponse);
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				out.print("{\"success\": false, \"message\": \"Invalid credentials.\"}");
			}
		} catch (Exception e) { //
			logger.error("Error during login", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"success\": false, \"message\": \"An error occurred during login.\"}");
		}
	}

	public void handleCheckIfExist(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try {
			// Parse request JSON vào object
			ExistsRequest existsRequest = objectMapper.readValue(request.getReader(), ExistsRequest.class);

			// Gọi service để lấy kết quả
			ExistsResponse result = authService.existsRespone(existsRequest.getField(), existsRequest.getValue());

			// Trả JSON về client
			out.print(objectMapper.writeValueAsString(result));
		} catch (StreamReadException | DatabindException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			out.print("{\"error\":\"Invalid request format\"}");
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\":\"Database error\"}");
		} catch (IOException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\":\"Server error\"}");
		}
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Trả lại đầy đủ header CORS
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		response.setHeader("Access-Control-Allow-Credentials", "true"); // nếu dùng cookie

		response.setStatus(HttpServletResponse.SC_OK);
	}
}