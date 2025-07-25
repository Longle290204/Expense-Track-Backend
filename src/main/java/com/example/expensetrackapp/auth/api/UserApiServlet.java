package com.example.expensetrackapp.auth.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.api.BaseApiServlet;
import com.example.expensetrackapp.auth.models.User;
import com.example.expensetrackapp.auth.models.UserRequest;
import com.example.expensetrackapp.auth.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/user/*")
public class UserApiServlet extends BaseApiServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(UserApiServlet.class);
	private static ObjectMapper objectMapper;
	private static UserService userService;

	@Override
	public void init() throws ServletException {
		super.init();
		objectMapper = new ObjectMapper();
		userService = new UserService();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String PathInfo = request.getPathInfo();

		PrintWriter out = response.getWriter();

		switch (PathInfo) {
		case "/getUserWithRolesAndPermission":
			try {
				handleGetUserWithRolesAndPermission(request, response, out);
			} catch (Exception e) {
				logger.error("Error in handleGetUserWithRolesAndPermission", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found in POST");
			break;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String PathInfo = request.getPathInfo();

		PrintWriter out = response.getWriter();

		switch (PathInfo) {
		case "/addUserToGroup":
			try {
				handleAddUserToGroup(request, response, out);
			} catch (Exception e) {
				logger.error("Error in handleAddRoleInGroup", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found in POST");
			break;
		}
	}

	public void handleAddUserToGroup(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {

		UserRequest userRequest = objectMapper.readValue(request.getReader(), UserRequest.class);
		if (userRequest.getGroup_id() == null || userRequest.getUser_id() == null) {
			out.print("{\"success\": false, \"message\": \"Enter missing data.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			userService.addUserToGroupService(userRequest.getGroup_id(), userRequest.getUser_id());
			out.print("{\"success\": true, \"message\": \"Role has been added to the group.\"}");
		} catch (RuntimeException e) {
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void handleGetUserWithRolesAndPermission(HttpServletRequest request, HttpServletResponse response,
			PrintWriter out) throws IOException {
		UserRequest userRequest = objectMapper.readValue(request.getReader(), UserRequest.class);
		if (userRequest.getUsername() == null || userRequest.getEmail() == null) {
			out.print("{\"success\": false, \"message\": \"Enter missing data.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			User user = userService.getUserWithRolAndPermisService(userRequest.getUsername(), userRequest.getEmail(),
					userRequest.getGroup_id());
			writeJsonResponse(response, user);
		} catch (RuntimeException e) {
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
