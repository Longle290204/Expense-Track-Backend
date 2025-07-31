package com.example.expensetrackapp.group.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.api.BaseApiServlet;
import com.example.expensetrackapp.auth.dao.UserDAO;
import com.example.expensetrackapp.auth.models.Group;
import com.example.expensetrackapp.auth.models.User;
import com.example.expensetrackapp.auth.services.JwtService;
import com.example.expensetrackapp.auth.utils.JwtUtil;
import com.example.expensetrackapp.group.model.GroupRequest;
import com.example.expensetrackapp.group.service.GroupService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.valueextraction.ExtractedValue;

@WebServlet("/api/group/*")
public class GroupServlet extends BaseApiServlet {

	private static final Logger logger = LoggerFactory.getLogger(GroupServlet.class);
	private GroupRequest groupRequest;
	private GroupService groupService;
	private UserDAO userDao;

	@Override
	public void init() throws ServletException {
		super.init();
		objectMapper = new ObjectMapper();
		groupService = new GroupService();
		userDao = new UserDAO();
		logger.info("AuthServlet initialized successfully");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		String pathInfo = request.getPathInfo();

		String[] pathParts = pathInfo.split("/");
		logger.info("pathinfo {}", pathInfo);
		if (pathInfo.equals("/create")) {
			logger.info("pathinfosdasdsad {}", pathInfo);
			handleCreateGroup(request, response, out);
		} else if (pathParts.length == 2) {
			UUID id = UUID.fromString(pathParts[1]);
			logger.info("gorupId {}", id);
			handleUpdateGroupById(id, request, response, out);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		String pathInfo = request.getPathInfo();

		switch (pathInfo) {
		case "/getAllGroup":
			try {
				handleGetAllGroup(request, response, out);
			} catch (Exception e) {
				logger.error("Error in handleGetAllGroup", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
		case "/getAllGroupUserId":
			try {
				handleGetAllGroupUserId(request, response, out);
			} catch (Exception e) {
				logger.error("Error in handleGetAllGroupUserId", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
		}

	}

//	@Override
//	protected void doPut(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		PrintWriter out = response.getWriter();
//		String pathInfo = request.getPathInfo();
//
//	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Path info missing");
			return;
		}

		String[] pathParts = pathInfo.split("/");
		if (pathParts.length >= 2) {
			try {
				UUID groupId = UUID.fromString(pathParts[1]);

				handleRomoveGroup(groupId, response, out);
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid group ID format");
			} catch (Exception e) {
				logger.error("Error deleting group", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Group ID not provided");
		}

	}

	public void handleCreateGroup(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	        throws IOException {

	    String user_id = null;

	    String authHeader = request.getHeader("Authorization");
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String token = authHeader.substring(7);
	        user_id = JwtUtil.extractUserId(token);
	    }

	    if (user_id == null) {
	        sendErrorResponse(response, 401, "{\"success\": false, \"message\": \"Unauthorized: Invalid token.\"}");
	        return;
	    }

	    GroupRequest groupRequest = objectMapper.readValue(request.getReader(), GroupRequest.class);

	    if (groupRequest.getGroup_name() == null || groupRequest.getGroup_name().trim().isEmpty()) {
	        sendErrorResponse(response, 400, "{\"success\": false, \"message\": \"Group name is required.\"}");
	        return;
	    }

	    try {
	        UUID groupId = groupService.createGroupService(groupRequest.getGroup_name(), user_id);

	        if (groupId != null) {
	            response.setStatus(HttpServletResponse.SC_OK);
	            out.print("{\"success\": true, \"message\": \"Create group successfully.\", \"group_id\": \"" + groupId + "\"}");
	            logger.info("Create group successfully with ID: {}", groupId);
	        } else {
	            response.setStatus(HttpServletResponse.SC_CONFLICT);
	            out.print("{\"success\": false, \"message\": \"Group already exists.\"}");
	        }
	    } catch (SQLException e) {
	        logger.error("Failed to create group", e);
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        out.print("{\"success\": false, \"message\": \"An error occurred creating group.\"}");
	    }
	}


	public void handleGetGroupById(String pathInfo, HttpServletResponse response) throws IOException {

	}

	public List<Group> handleGetAllGroupUserId(HttpServletRequest request, HttpServletResponse response,
			PrintWriter out) throws IOException {

		List<Group> groups = new ArrayList<>();

		String user_id = null;
		String authHeader = request.getHeader("Authorization");

		try {
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				out.print("{\"error\": \"Missing or invalid Authorization header\"}");
				return groups;
			}

			String token = authHeader.substring(7);

			user_id = JwtUtil.extractUserId(token);

			logger.info("userid {}", user_id);

			groups = groupService.getAllGroupUserIdService(UUID.fromString(user_id));
			writeJsonResponse(response, groups);
		} catch (RuntimeException e) {
			logger.warn("JWT error: {}", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			out.print("{\"error\": \"" + e.getMessage() + "\"}");
			return groups;
		}

		return groups;
	}

	public void handleGetUserInGroup(HttpServletRequest request, HttpServletResponse response) throws IOException {

		UUID user_id = null;

		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			user_id = UUID.fromString(JwtUtil.extractUserId(token));
		}
	}

	public List<Group> handleGetAllGroup(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {

		List<Group> groups = new ArrayList<>();
		try {
			groups = groupService.getAllGroupService();
			writeJsonResponse(response, groups);
		} catch (RuntimeException e) {
			logger.error("Error when handle get all group role", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return groups;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @throws IOException
	 */
	public void handleRomoveGroup(UUID groupId, HttpServletResponse response, PrintWriter out) throws IOException {

		try {
			groupService.removeGroupById(groupId);
			response.setStatus(HttpServletResponse.SC_OK);
			out.print("{\"success\": true, \"message\": \"Delete group successfully.\"}");
			logger.info("Delete group  successfully");
		} catch (RuntimeException e) {
			logger.error("Error when handle remove group role", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void handleUpdateGroupById(UUID id, HttpServletRequest request, HttpServletResponse response,
			PrintWriter out) throws IOException {
		try {
			groupRequest = objectMapper.readValue(request.getReader(), GroupRequest.class);
			if (groupRequest.getGroup_name() == null) {
				out.print("{\"success\": false, \"message\": \"Data is missing required.\"}");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			groupService.updateGroupNameService(groupRequest.getGroup_name(), id);
			response.setStatus(HttpServletResponse.SC_OK);
			out.print("{\"success\": true, \"message\": \"Update group successfully.\"}");
			logger.info("Update group  successfully");
		} catch (RuntimeException e) {
			logger.error("Error when handle Update group role", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
