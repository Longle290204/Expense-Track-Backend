package com.example.expensetrackapp.auth.api;

import java.io.IOException;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.dao.RefreshTokenDAO;
import com.example.expensetrackapp.auth.models.RefreshTokenRecord;
import com.example.expensetrackapp.auth.models.RefreshTokenRequest;
import com.example.expensetrackapp.auth.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/refresh-token")
public class RefreshTokenServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServlet.class);
	private JwtService jwtService;
	private ObjectMapper objectMapper;
	private RefreshTokenDAO refreshTokenDao;

	@Override
	public void init() throws ServletException {
		this.jwtService = new JwtService();
		this.objectMapper = new ObjectMapper();
		this.refreshTokenDao = new RefreshTokenDAO();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();

		// Map refresh token từ request vào object
		RefreshTokenRequest refreshTokenRequest = objectMapper.readValue(request.getReader(),
				RefreshTokenRequest.class);
		String refreshToken = refreshTokenRequest.getRefreshToken();
		logger.info("refreshtoken {}", refreshToken);
		try {
			// Map json từ body
			if (refreshToken == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("RefresgToken is required");
				return;
			}

			// Kiểm tra refresh token trong DB
			String username = refreshTokenRequest.getUsername();
			Optional<RefreshTokenRecord> opt = refreshTokenDao.findByToken(refreshToken);

			logger.info("opt {}", opt);
			if (opt.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.print("Invalid or expired refresh token");
				return;
			}

			RefreshTokenRecord record = opt.get();
			String userId = record.getUserId().toString();
			logger.info("expiresAt = {}, now = {}", record.getExpiresAt(), LocalDateTime.now());
			// Check thời hạn
			if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
				
				// Hết hạn -> xóa khỏi database
				refreshTokenDao.deleteByToken(refreshToken);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				out.print("{\"error\":\"Refresh token expired. Please login again.\"}");
				return;
			}

			// Còn hạn, tạo access token mới
			// Tạo access token mới
			String newAccessToken = jwtService.generateAccessToken(userId, username);

			// (Optional) Rotate refresh token
			String newRefreshToken = jwtService.generateRefreshToken(userId, newAccessToken);
			LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(7);

			refreshTokenDao.rotateRefreshToken(record.getId(), newRefreshToken, newExpiresAt);

			response.setContentType("application/json");
			String json = "{ \"accessToken\": \"" + newAccessToken + "\", \"refreshToken\": \"" + newRefreshToken
					+ "\" }";
			out.print(json);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"error\": \"Invalid or expired refresh token\"}");
		}
	}
}
