package com.example.expensetrackapp.auth.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.models.Permission;
import com.example.expensetrackapp.auth.models.Role;
import com.example.expensetrackapp.config.DBConnection;

public class RoleDAO {

	private static final Logger logger = LoggerFactory.getLogger(RoleDAO.class);

	/**
	 * Tạo một vai trò mới trong DB.
	 */
	public Role addRoleInGroup(String role_name, UUID group_id, Boolean is_system) throws SQLException {
		String sql = "INSERT INTO Roles (role_name, group_id, is_system) VALUES (?, ?, ?)";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, role_name);
			pstmt.setObject(2, group_id);

			// Trường hợp không gửi is_system từ client. Trường hợp dành cho user thêm role
			// mới vào group
			if (is_system == null) {
				is_system = false;
			}

			pstmt.setBoolean(3, is_system);
			int rowUpdate = pstmt.executeUpdate();

			if (rowUpdate > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						Role role = new Role();
						role.setRoleId(generatedKeys.getString(1));
						return role;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Lấy tất cả các vai trò từ DB.
	 * 
	 * @Note: Thực hiện cho việc hiển thị tát cả role để admin có thể thêm vào các
	 *        thành viên
	 * 
	 * 
	 */
	public List<Role> getAllRoles() throws SQLException {
		List<Role> roles = new ArrayList<>();

		String sql = "SELECT role_id, role_name FROM roles ORDER BY role_name";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				roles.add(new Role(rs.getString("role_id"), rs.getString("role_name")));
			}
		}
		return roles;
	}

	/**
	 * Get all roles in group
	 * 
	 * @param group_id
	 * @return
	 * @throws SQLException
	 */
	public List<Role> getAllRolesInGroup(String group_id) throws SQLException {
		List<Role> roles = new ArrayList<>();

		String sql = "SELECT role_id, role_name FROM roles WHERE group_id = ? ORDER BY role_name";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql);) {
			pstmt.setString(1, group_id);
			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					roles.add(new Role(rs.getString("role_id"), rs.getString("role_name")));
				}
			}

		}
		return roles;
	}

	/**
	 * 
	 * @Note: Lấy một vai trò theo ID, bao gồm cả các quyền được gán cho nó trong
	 *        group nhất định.
	 * 
	 * @return: Return object role and permission in tree format
	 * 
	 */
	public Role getRolePermissionByIdInGroup(String role_id, String group_id) throws SQLException {
		Role role = null;
		if (role == null) {
			logger.warn("Role with ID {} and group ID {} not found", role_id, group_id);
		}

		String sql = "SELECT r.role_id, r.role_name, p.permission_id, p.permission_name " + "FROM roles r "
				+ "LEFT JOIN role_permissions rp ON r.role_id = rp.role_id AND r.group_id = rp.group_id"
				+ "LEFT JOIN permissions p ON rp.permission_id = p.permission_id AND rp.group_id = p.group_id"
				+ "WHERE r.role_id = ? AND group_id = ?";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql);) {
			pstmt.setString(1, role_id);
			pstmt.setString(2, group_id);

			try (ResultSet rs = pstmt.executeQuery()) {
				Set<Permission> permissions = new HashSet<>();
				while (rs.next()) {
					if (role == null) {
						role = new Role(rs.getString("role_id"), rs.getString("role_name"));
					}
					String permId = rs.getString("permission_id");
					if (permId != null) {
						permissions.add(new Permission(UUID.fromString(rs.getString("permission_id")),
								rs.getString("permission_name")));
					}
				}
				if (role != null) {
					role.setPermissions(permissions);
				}
			}
		}
		return role;
	}

	/**
	 * 
	 * Cập nhật thông tin vai trò.
	 * 
	 * @param: role_id, role_name
	 * @throws SQLException
	 * @Note: Update role name
	 * 
	 */
	public boolean updateRoleInGroup(UUID role_id, String role_name, UUID group_id) throws SQLException {
		String sql = "UPDATE roles SET role_name = ? WHERE role_id = ? AND group_id = ?";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {

			pstmt.setString(1, role_name);
			pstmt.setObject(2, role_id);
			pstmt.setObject(3, group_id);

			if (pstmt.executeUpdate() > 0) {
				logger.info("Update role {} is successfully", role_name);
				return true;
			}
		}
		return false;
	}

	/**
	 * Xóa vai trò trong group
	 * 
	 * @param role_id vai trò cần xóa
	 * @return true nếu xóa thành công, false nếu không tìm thấy vai trò
	 * @throws SQLException nếu có lỗi truy vẫn DB
	 * 
	 */
	public boolean deleteRoleInGroup(UUID role_id, UUID group_id) throws SQLException {
		String sql = "DELETE FROM roles WHERE role_id = ? AND group_id = ?";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {

			pstmt.setObject(1, role_id);
			pstmt.setObject(2, group_id);
			return pstmt.executeUpdate() > 0;
		}
	}

	/**
	 * 
	 * Add permission from role within range group
	 * 
	 * @param role_id
	 * @param permissionIds
	 * @throws SQLException
	 * 
	 */
	public boolean addPermissionToRoleInGroup(UUID role_id, UUID group_id, List<UUID> permissionIds)
			throws SQLException {
		String sql = "INSERT INTO role_permissions (role_id, permission_id, group_id) VALUES (?, ?, ?)";

		if (permissionIds == null || permissionIds.isEmpty()) {
			return false;
		}

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			for (UUID permissionId : permissionIds) {
				pstmt.setObject(1, role_id);
				pstmt.setObject(2, permissionId);
				pstmt.setObject(3, group_id);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			return true;
		} catch (SQLException e) {
			logger.error("Failed to add permission to role in group", e);
		}

		return false;
	}

	/**
	 * 
	 * Remove permission from role within range group
	 * 
	 * @param role_id
	 * @param group_id
	 * @param permissionIds
	 * @throws SQLException
	 * 
	 */
	public void removePermissionFromRoleInGroup(String role_id, String group_id, List<String> permissionIds)
			throws SQLException {
		if (role_id == null || group_id == null || permissionIds == null || permissionIds.isEmpty()) {
			return;
		}

		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < permissionIds.size(); i++) {
			placeholders.append('?');
			if (i < permissionIds.size() - 1) {
				placeholders.append(",");
			}
		}

		String sql = "DELETE FROM role_permissions WHERE role_id = ? AND group_id = ? AND permission_id IN ("
				+ placeholders + ")";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setString(1, role_id);

			for (int i = 0; i < permissionIds.size(); i++) {
				pstmt.setString(i + 2, permissionIds.get(i));
			}

			int affectedRows = pstmt.executeUpdate();
			logger.info("Removed " + affectedRows + " permission(s) from role in group");
		}
	}

	/**
	 * Get permission from role in group
	 * 
	 * So that frontend can see all the permissions of a specific role
	 * 
	 * @param role_id Id của vai trò
	 * @return set permission của role cụ thể
	 * @throws SQLException
	 */
	public Set<Permission> getPermissionFromRoleInGroup(UUID role_id, UUID group_id) throws SQLException {

		Set<Permission> permissions = new HashSet<>();

		String sql;

		if (group_id == null) {
			sql = "SELECT p.permission_id, p.permission_name FROM permissions p "
					+ "JOIN role_permissions rp ON rp.permission_id = p.permission_id "
					+ "WHERE rp.role_id = ? AND rp.group_id IS NULL";
		} else {
			sql = "SELECT p.permission_id, p.permission_name FROM permissions p "
					+ "JOIN role_permissions rp ON rp.permission_id = p.permission_id "
					+ "WHERE rp.role_id = ? AND rp.group_id = ?";
		}

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {

			pstmt.setObject(1, role_id);
			if (group_id != null) {
				pstmt.setObject(2, group_id);
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					permissions.add(new Permission(UUID.fromString(rs.getString("permission_id")),
							rs.getString("permission_name")));
				}
			}
		}
		return permissions;
	}

	/**
	 * Lấy ra permission của người dùng trong nhóm
	 * 
	 * @param user_id  ID người dùng
	 * @param group_id ID nhóm
	 * @return return permission array
	 * 
	 */
	public List<String> getUserPermissionsInGroup(UUID user_id, UUID group_id) throws SQLException {

		List<String> permissions = new ArrayList<>();
		String sql = "SELECT DISTINCT p.permission_name " + "FROM permission p "
				+ "JOIN role_permissions rp ON rp.permission_id = p.permission_id AND rp.group_id = p.group_id "
				+ "JOIN user_roles ur ON ur.role_id = rp.role_id AND rp.group_id = ur.group_id "
				+ "WHERE user_id = ? AND group_id = ?";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql);) {
			pstmt.setObject(1, user_id);
			pstmt.setObject(2, group_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					permissions.add(rs.getString("permission_name"));
				}
			} catch (SQLException e) {
				logger.error("Error getting user permissions for user " + user_id + " in group " + group_id, e);
			}
		}
		return permissions;
	}

	/**
	 * Get roles of user in group
	 * 
	 * @param user_id
	 * @param group_id
	 * @return roles
	 */
	public List<String> getUserRolesInGroup(UUID user_id, UUID group_id) {
		List<String> roles = new ArrayList<>();
		String sql = "SELECT DISTINCT r.role_name " + "FROM roles r "
				+ "JOIN user_roles ur ON ur.user_id = r.user_id AND AND ur.group_id = r.group_id "
				+ "WHERE user_id = ? AND group_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setObject(1, user_id);
			pstmt.setObject(2, group_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					roles.add(rs.getString("role_name"));
				}
			}
		} catch (SQLException e) {
			logger.error("Error getting user roles for user " + user_id + " in group " + group_id, e);
		}
		return roles;
	}
	
	/**
	 * Gán thêm các vai trò cho một người dùng trong group riêng biệt
	 * 
	 * @param user_id Id của người dùng
	 * @param role_id Id vai trò
	 * @throws SQLException
	 * @Note: - Sử dụng batch để gửi dữ liệu cùng lúc tránh gửi query tới database
	 *        nhiều lần - Không thể dùng IN để chèn nhiều bản ghi cùng lúc (vì
	 *        INSERT cần rõ từng cặp giá trị).
	 */
	public boolean assignRolesToUserDao(UUID user_id, UUID group_id, UUID role_id) throws SQLException {

		String sql = "INSERT INTO user_roles (user_id, role_id, group_id) VALUES (?, ?, ?)";
		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {

			pstmt.setObject(1, user_id);
			pstmt.setObject(2, role_id);
			pstmt.setObject(3, group_id);
			int rowUpdates = pstmt.executeUpdate();
			
			return rowUpdates > 0;
		}
	}
}
