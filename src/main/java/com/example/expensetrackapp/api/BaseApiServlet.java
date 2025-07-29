package com.example.expensetrackapp.api;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class BaseApiServlet extends HttpServlet {
	// ObjectMapper là thread-safe, nên có thể dùng chung
	protected ObjectMapper objectMapper;

	public BaseApiServlet() {
		this.objectMapper = new ObjectMapper();
		// Cấu hình để Pretty Print JSON (dễ đọc hơn trong quá trình phát triển)
		this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		// Đăng ký JavaTimeModule để Jackson có thể xử lý LocalDateTime (cho User model)
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	/**
	 * Ghi một đối tượng Java thành JSON vào HttpServletResponse.
	 * 
	 * @param response HttpServletResponse để ghi JSON
	 * @param object   Đối tượng Java cần chuyển đổi thành JSON
	 * @throws IOException Nếu có lỗi khi ghi response
	 */
	protected void writeJsonResponse(HttpServletResponse response, Object object) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), object);
	}

	/**
	 * Ghi một thông báo lỗi JSON vào HttpServletResponse.
	 * 
	 * @param response   HttpServletResponse
	 * @param statusCode Mã trạng thái HTTP (ví dụ: 400, 404, 500)
	 * @param message    Thông báo lỗi
	 * @throws IOException Nếu có lỗi khi ghi response
	 */
	protected void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
		response.setStatus(statusCode);
		writeJsonResponse(response, Map.of("success", false, "message", message));
	}

}
