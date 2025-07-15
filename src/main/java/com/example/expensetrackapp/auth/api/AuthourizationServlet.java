package com.example.expensetrackapp.auth.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.models.Role;
import com.example.expensetrackapp.auth.models.RoleResponse;
import com.example.expensetrackapp.auth.services.AuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/role/*")
public class AuthourizationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(AuthourizationServlet.class);
	private ObjectMapper objectMapper;
	private AuthorizationService authorizationService;

	@Override
	public void init() throws ServletException {
		super.init();
		authorizationService = new AuthorizationService();
		objectMapper = new ObjectMapper();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
		case "/create-role":
			createRoleServlet(request, response, out);
			break;
		default:
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			out.print("{\"success\": false, \"message\": \"API endpoint not found.\"}");
			break;
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	public void createRoleServlet(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {

		try {
			Role role = objectMapper.readValue(request.getReader(), Role.class);

			if (authorizationService.addRole(role.getRoleName())) {
				response.setStatus(HttpServletResponse.SC_CREATED);
				
				RoleResponse roleResponse = new RoleResponse();
				roleResponse.setRoleName(role.getRoleName());
				
				out.print(objectMapper.writeValueAsString(roleResponse));
			} else {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				out.print("{\"success\": false, \"message\": \"Role already exists\".}");
			}

		} catch (IOException e) {
			logger.error("Error during add role", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"success\": false, \"message\": \"An error occurred during registration.\"}");
		}
	}

}
