package com.example.expensetrackapp.filters;

import java.io.IOException;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.services.JwtService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/api/protected/*")
public class JwtAuthFilter extends HttpFilter implements Filter {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
	private JwtService jwtService;

	public void init(FilterConfig fConfig) throws ServletException {
		jwtService = new JwtService();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String authHeader = httpRequest.getHeader("Authourization");
		String token = null;

		if (authHeader != null && authHeader.startsWith("Bearer")) {
			token = authHeader.substring(7);
		}

		if (token == null || !jwtService.isTokenValid(token)) {
			logger.warn("Unauthorized access attemp.....");
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			httpResponse.setContentType("application/json");
			httpResponse.getWriter()
					.write("{\"success\": false, \"message\": \"Unauthorized: Invalid or missing token.\"}");
			return;
		}

		String username = jwtService.extractUsername(token);
		String role = (String) jwtService.extractAllClaims(token).get("role");

		request.setAttribute("username", username);
		request.setAttribute("role", role);

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}