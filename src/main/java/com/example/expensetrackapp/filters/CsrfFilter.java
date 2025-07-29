package com.example.expensetrackapp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/*")
public class CsrfFilter implements Filter{

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletRequest;
		
		// Chỉ kiểm tra CSRF với POST, PUT, DELETE
		String method = request.getMethod();
		
		if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
			
			// Lấy token từ cookie
			String csrfCookie = null;
			if (request.getCookies() != null) {
				for (Cookie cookie : request.getCookies()) {
					if (cookie.getName().equals("XSRF-TOKEN")) {
						csrfCookie = cookie.getValue();
						break;
					}
				}
			}
			
			// Lấy token từ header
			String csrfHeader = request.getHeader("X-XSRF-TOKEN");
			
			// So sánh
			if (csrfCookie == null || !csrfCookie.equals(csrfHeader)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("CSRF token không hợp lệ");
                return;
			}
			
			// Kiểm tra trong session
			String sessionToken = (String) request.getSession().getAttribute("CSRF_TOKEN");
			 if (sessionToken == null || !sessionToken.equals(csrfCookie)) {
	                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	                response.getWriter().write("CSRF session token không hợp lệ");
	                return;
	            }
		}
		 chain.doFilter(request, response);
	}
}
