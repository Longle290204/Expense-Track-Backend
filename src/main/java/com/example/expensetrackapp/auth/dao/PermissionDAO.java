package com.example.expensetrackapp.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.models.Permission;
import com.example.expensetrackapp.config.DBConnection;

public class PermissionDAO {
	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

	public boolean addPermission(String permission_name, String group_name) throws SQLException {
		String sql = "INSERT INTO permissions (permission_name, group_name) VALUES (?, ?)";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setString(1, permission_name);
			pstmt.setString(2, group_name);
			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				return true;
			}
		} catch (SQLException e) {
			logger.error(">>> DAO LỖI: " + e.getMessage()); // THÊM DÒNG NÀY
			throw e;
		}

		return false;
	}

	/**
	 * 
	 * @return Return all permissions
	 * @throws SQLException throws error if database error
	 * 
	 */
	public List<Permission> getAllPermissions() throws SQLException {
		List<Permission> permissions = new ArrayList<>();
		String sql = "SELECT permission_id, permission_name FROM permissions ORDER BY permission_name";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				permissions.add(new Permission(UUID.fromString(rs.getString("permission_id")), rs.getString("permission_name")));
			}
			logger.info("{\"success\": true, \"message\": \"Get all permission\"}");
			logger.info("get all permission {}", permissions);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Fail to query getAllPermissions");
		}

		return permissions;
	}

	/**
	 * 
	 * @param permission_id
	 * @return
	 * @throws SQLException
	 * 
	 */
	public Permission getPermissionById(UUID permission_id) throws SQLException {
		String sql = "SELECT permission_id, permission_name FROM permissions WHERE permission_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setObject(1, permission_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Permission permission = new Permission();
					permission.setPermissionId(UUID.fromString(rs.getString("permission_id")));
					permission.setPermissionName(rs.getString("permission_name"));

					logger.info("Get permission '{}' by id {}", permission.getPermissionName(), permission_id);
					return permission;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param permission_name
	 * @return
	 * @throws SQLException
	 * 
	 */
	public Permission getPermissionByName(String permission_name) throws SQLException {
		String sql = "SELECT permission_id, permission_name FROM permissions WHERE permission_name = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, permission_name);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Permission permission = new Permission();
					permission.setPermissionId(UUID.fromString(rs.getString("permission_id")));
					permission.setPermissionName(rs.getString("permission_name"));

					logger.info("Get permission '{}' by name {}", permission_name, permission_name);
					return permission;
				}
			} catch (SQLException e) {
				logger.error("Failed to get permission by name: {}", permission_name, e);
				throw e;
			}
		}
		return null;
	}
}
