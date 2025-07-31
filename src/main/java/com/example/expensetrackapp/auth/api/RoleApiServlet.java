package com.example.expensetrackapp.auth.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.api.BaseApiServlet;
import com.example.expensetrackapp.auth.dao.RoleDAO;
import com.example.expensetrackapp.auth.models.Permission;
import com.example.expensetrackapp.auth.models.Role;
import com.example.expensetrackapp.auth.models.RoleRequest;
import com.example.expensetrackapp.auth.services.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/role/*")
public class RoleApiServlet extends BaseApiServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(RoleApiServlet.class);
	private ObjectMapper objectMapper;
	private RoleService roleService;

	@Override
	public void init() throws ServletException {
		super.init();
		objectMapper = new ObjectMapper();
		roleService = new RoleService();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		PrintWriter out = response.getWriter();

		String pathInfo = request.getPathInfo();

		switch (pathInfo) {
		case "/addRoleInGroup":
			try {
				handleAddRoleInGroup(request, response, out);
			} catch (Exception e) {
				logger.error("Error in handleAddRoleInGroup", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
			break;
		case "/addPermissionToRole":
			try {
				handleAddPermissionToRoleInGroup(request, response, out);
			} catch (Exception e) {
				logger.error("Error in handleAddPermissionToRoleInGroup", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
			break;
		case "/assignRoleToUser":
			try {
				handleAssignRoleToUser(request, response, out);
			} catch (Exception e) {
				logger.error("Error in handleAssignRoleToUser", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
			break;
		case "/updatePermissionForRole":
			try {
				handleUpdatePermissionForRole(request, response, out);
			} catch (Exception e) {
				logger.error("Error in handleUpdatePermissionForRole", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
			}
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found in POST");
			break;
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		PrintWriter out = response.getWriter();

		String pathInfo = request.getPathInfo();

		if (pathInfo != null) {
			String[] pathPart = pathInfo.split("/");

			// pathPart[0] = "", pathPart[1] = "handleGetAllRolesInGroup", pathPart[2] =
			// group_id
			if (pathPart.length >= 3 && "getAllRolesInGroup".equals(pathPart[1])) {
				handleGetAllRoleInGroup(response, out, (UUID.fromString(pathPart[2])));
				return;
			}

			switch (pathInfo) {
			case "/getPermissionFromRole":
				try {
					handleGetPermissionFromRoleInGroup(request, response, out);
				} catch (Exception e) {
					logger.error("Error in handleGetPermissionFromRoleInGroup", e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
				}
				break;
			case "/getAllRolesSystem":
				try {
					handleGetAllRoleSystem(response, out);
				} catch (Exception e) {
					logger.error("Error in handleGetAllRoleSystem", e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
				}
				break;
			}

			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found in GET");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		String pathInfo = request.getPathInfo();

		if (pathInfo == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Path is missiong");
			return;
		}

		switch (pathInfo) {
		case "/deleteRoleInGroup":
			handleDeleteRoleInGroup(request, response, out);
			break;

		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found in DELETE");
			break;
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String pathInfo = request.getPathInfo();

		if (pathInfo == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Path is missiong");
			return;
		}

		switch (pathInfo) {
		case "/updateRoleInGroup":
			handleUpdateRoleInGroup(request, response, out);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found in DELETE");
			break;
		}
	}

	/**
	 * ADD ROLE IN GROUP
	 * 
	 * @throws IOException
	 */
	public boolean handleAddRoleInGroup(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException, SQLException {
		RoleRequest roleRequest = objectMapper.readValue(request.getReader(), RoleRequest.class);

		if (roleRequest.getRole_name() == null && roleRequest.getGroup_id() == null) {
			out.print("{\"success\": false, \"message\": \"Role_name and group_id are required.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		}

		try {
			roleService.addRoleInGroupService(roleRequest.getRole_name(), roleRequest.getGroup_id(),
					roleRequest.getIs_system());
			out.print("{\"success\": true, \"message\": \"Role has been added to the group.\"}");
		} catch (RuntimeException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		}

		return false;
	}

	/**
	 * DELETE ROLE IN GROUP
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @throws IOException
	 */
	public void handleDeleteRoleInGroup(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {

		RoleRequest roleRequest = objectMapper.readValue(request.getReader(), RoleRequest.class);

		if (roleRequest.getGroup_id() == null || roleRequest.getRole_id() == null) {
			out.print("{\"success\": false, \"message\": \"Role_id and group_id are required.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			roleService.deleteRoleInGroupService(roleRequest.getGroup_id(), roleRequest.getRole_id());
			out.print("{\"success\": true, \"message\": \"Role deleted successfully\"}");
		} catch (RuntimeException e) {
			logger.error("Lỗi khi xử lý xóa role", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * UPDATE ROLE IN GROUP
	 * 
	 * @param request
	 * @param response
	 * @throws IOExceptions
	 */
	public void handleUpdateRoleInGroup(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {
		RoleRequest roleRequest = objectMapper.readValue(request.getReader(), RoleRequest.class);
		if (roleRequest.getRole_id() == null || roleRequest.getRole_name() == null
				|| roleRequest.getGroup_id() == null) {
			out.print("{\"success\": false, \"message\": \"Role_id and group_id are required.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			roleService.updateRoleInGroupService(roleRequest.getRole_id(), roleRequest.getRole_name(),
					roleRequest.getGroup_id());
			out.print("{\"success\": true, \"message\": \"Role updated successfully\"}");
		} catch (RuntimeException e) {
			logger.error("Error when handle update role", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @throws IOException
	 */
	public void handleAddPermissionToRoleInGroup(HttpServletRequest request, HttpServletResponse response,
			PrintWriter out) throws IOException {
		RoleRequest roleRequest = objectMapper.readValue(request.getReader(), RoleRequest.class);
		if (roleRequest.getRole_id() == null || roleRequest.getPermissionIds().isEmpty()) {
			out.print("{\"success\": false, \"message\": \"Role_id and group_id are required.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			roleService.addPermissionToRoleInGroupService(roleRequest.getRole_id(), roleRequest.getGroup_id(),
					roleRequest.getPermissionIds());
		} catch (RuntimeException e) {
			logger.error("Error when handle add permission for role", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param out
	 * @throws IOException
	 */
	public void handleGetPermissionFromRoleInGroup(HttpServletRequest request, HttpServletResponse response,
			PrintWriter out) throws IOException {

		RoleRequest roleRequest = objectMapper.readValue(request.getReader(), RoleRequest.class);
		if (roleRequest.getRole_id() == null) {
			out.print("{\"success\": false, \"message\": \"Role_id and group_id are required.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			List<Permission> permission = roleService.getPermissionFromRoleInGroupService(roleRequest.getRole_id(),
					roleRequest.getGroup_id());
			writeJsonResponse(response, permission);
		} catch (RuntimeException e) {
			logger.error("Error when handle get permission from role", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
//	String user_id, String group_id, String role_id

	public void handleAssignRoleToUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {
		RoleRequest roleRequest = objectMapper.readValue(request.getReader(), RoleRequest.class);
		if (roleRequest.getRole_id() == null || roleRequest.getGroup_id() == null || roleRequest.getUser_id() == null) {
			out.print("{\"success\": false, \"message\": \"Enter missing field.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			roleService.assignRoleToUserService(roleRequest.getUser_id(), roleRequest.getGroup_id(),
					roleRequest.getRole_id());
			out.print("{\"success\": false, \"message\": \"Role_id and group_id are required.\"}");
		} catch (RuntimeException e) {
			logger.error("Error when handle get permission from role", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void handleGetAllRoleInGroup(HttpServletResponse response, PrintWriter out, UUID group_id)
			throws IOException {

		if (group_id == null) {
			out.print("{\"success\": false, \"message\": \"Enter missing field.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		List<Role> roles = null;

		try {

			roles = roleService.getAllRoleInGroupService(group_id);

			writeJsonResponse(response, roles);
		} catch (RuntimeException e) {
			logger.error("Error when handle get all roles", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void handleGetAllRoleSystem(HttpServletResponse response, PrintWriter out) throws IOException {

		List<Role> roles = null;

		try {
			roles = roleService.getAllRoleSystemService();
			writeJsonResponse(response, roles);
		} catch (RuntimeException e) {
			logger.error("Error when handle get all roles system", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void handleUpdatePermissionForRole(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
			throws IOException {

		RoleRequest roleRequest = objectMapper.readValue(request.getReader(), RoleRequest.class);
		if (roleRequest.getRole_id() == null || roleRequest.getGroup_id() == null
				|| roleRequest.getPermissionIds() == null) {
			out.print("{\"success\": false, \"message\": \"Enter missing field.\"}");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			roleService.updatePermissionForRol(roleRequest.getRole_id(), roleRequest.getGroup_id() , roleRequest.getPermissionIds());
		} catch (RuntimeException e) {
			logger.error("Error when handle get all roles", e);
			out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
