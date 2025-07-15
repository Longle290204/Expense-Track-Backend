package com.example.expensetrackapp.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*") 
public class CorsFilter implements Filter {
	 @Override
	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	            throws IOException, ServletException {

	        HttpServletResponse res = (HttpServletResponse) response;

	        res.setHeader("Access-Control-Allow-Origin", "*"); // Cho phép mọi domain. Có thể thay * bằng domain cụ thể
	        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
	        res.setHeader("Access-Control-Max-Age", "3600");

	        // Nếu là preflight request (OPTIONS), chỉ trả header và kết thúc
	        chain.doFilter(request, response);
	    }

	    @Override
	    public void init(FilterConfig filterConfig) {}

	    @Override
	    public void destroy() {}
}
