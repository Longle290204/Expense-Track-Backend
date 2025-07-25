package com.example.expensetrackapp.auth.api;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.api.BaseApiServlet;
import com.example.expensetrackapp.auth.dao.PermissionDAO;
import com.example.expensetrackapp.auth.models.Permission;
import com.example.expensetrackapp.auth.models.PermissionRequest;
import com.example.expensetrackapp.auth.services.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/permissions/*")
public class PermissionServlet extends BaseApiServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(PermissionServlet.class);
	private ObjectMapper objectMapper;
	private PermissionDAO permissionDao = new PermissionDAO();
	private Permission permission = null;
	private PermissionService permissionService;

	@Override
	public void init() throws ServletException {
		super.init();
		objectMapper = new ObjectMapper();
		permissionService = new PermissionService();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		String pathInfo = request.getPathInfo();

		switch (pathInfo) {
		case "/addPermission":
			handleAddPermission(request, response, out);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found in POST");
			break;
		}

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String pathInfo = request.getPathInfo();

		try {
			if (pathInfo == null || pathInfo.equals("/")) {
				sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Permission ID or route is required.");
	            return;
			}
			
			if (pathInfo.equals("/getAllPermissions")) {
	            handleGetAllPermissions(response, out);
	            return;
	        }
			
			handleGetPermissionById(pathInfo, response);
			
		} catch (Exception e) {
			 sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error occurred.");
		}
	}

	/**
	 * 
	 * @param request
	 * @param response return to client
	 * @param out      return json format
	 * @throws IOException
	 */
	public void handleGetAllPermissions(HttpServletResponse response, PrintWriter out) throws IOException {
		try {
			List<Permission> permissions = permissionDao.getAllPermissions();
			logger.info("All permission {}", permissions);
			writeJsonResponse(response, permissions);

		} catch (SQLException e) {
			logger.error("Fail to get all permissions", e);
			out.print("{\"success\": false, \"message\": \"Fail to get all permission.\"}");
		}
	}

	public void handleGetPermissionById(String pathInfo, HttpServletResponse response) throws IOException {
		String[] pathParts = pathInfo.split("/");

		if (pathParts.length != 2) {
			sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid permission ID format.");
			return;
		}
		
		UUID permissionId = UUID.fromString(pathParts[1]);

		try {
			permission = permissionDao.getPermissionById(permissionId);
			writeJsonResponse(response, permission);
		} catch (SQLException e) {
			sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Permission not found.");
			e.printStackTrace();
		}
	}

	/**
	 * For system
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @throws IOException
	 */
	public void handleAddPermission(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {
		PermissionRequest permissionRequest = objectMapper.readValue(request.getReader(), PermissionRequest.class);
		if (permissionRequest.getPermission_name() == null || permissionRequest.getGroup_name() == null) {
			out.print("{\"success\": false, \"message\": \"Role_id and group_id are required.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			permissionService.addPermission(permissionRequest.getPermission_name(), permissionRequest.getGroup_name());
			out.print("{\"success\": true, \"message\": \"Add permission success.\"}");
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (RuntimeException e) {
			logger.error("Error operation when add permission");
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		} catch (Exception e) {
			logger.error("unknown error", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"success\": false, \"message\": \"Error system.\"}");
		}
	}
}
