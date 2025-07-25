package com.example.expensetrackapp.group.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
			handleGetGroupById(pathInfo, response);
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
		
		logger.info("user_id {}", user_id);
		
		if (user_id == null) {
		    sendErrorResponse(response, 401, "{\"success\": false, \"message\": \"Unauthorized: Invalid token.\"}");
		    return;
		}

		groupRequest = objectMapper.readValue(request.getReader(), GroupRequest.class);

		if (groupRequest.getGroup_name() == null) {
			sendErrorResponse(response, 400, "{\"success\": false, \"message\": \"Info group are required.\"}");
			return;
		}
		
		try {
			if (groupService.createGroupService(groupRequest.getGroup_name(), user_id)) {
				response.setStatus(HttpServletResponse.SC_OK); 
				out.print("{\"success\": true, \"message\": \"Create group successfully.\"}");
				logger.info("create group  successfully");
			}
		} catch (SQLException e) {
			logger.warn("Faild to create group");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"success\": false, \"message\": \"An error occurred creating group.\"}");
		}
	}

	public void handleGetGroupById(String pathInfo, HttpServletResponse response) throws IOException {

	}
	
	public void handleGetUserInGroup(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		UUID user_id = null;
		
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			user_id = UUID.fromString(JwtUtil.extractUserId(token)) ;
		}
	}
	
	public List<Group> handleGetAllGroup(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {

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
}
