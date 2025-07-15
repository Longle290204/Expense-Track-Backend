package com.example.expensetrackapp.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.models.Permission;
import com.example.expensetrackapp.config.DBConnection;

public class PermissionDAO {
	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

	public List<Permission> getAllPermissions() throws SQLException {
		List<Permission> permissions = new ArrayList<>();
		String sql = "SELECT permission_id, permission_name FROM permissions ORDER BY permission_name";
		Connection connect = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			connect = DBConnection.getConnection();
			pstmt = connect.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				permissions.add(new Permission(rs.getString("permission_id"), rs.getString("permission_name")));
			}
		} catch (SQLException e) {
			logger.error("Fail to query getAllPermissions");

		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (connect != null) {
					connect.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return permissions;
	}

	public Permission getPermissionById(String permission_id) throws SQLException {
		String sql = "SELECT permission_id, permission_name FROM Permissions WHERE permission_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, permission_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Permission permission = new Permission();
					permission.setPermissionId(rs.getString("permission_id"));
					permission.setPermissionName(rs.getString("permission_name"));

					logger.info("Get permission '{}' by id {}", permission.getPermissionName(), permission_id);
					return permission;
				}
			} catch (SQLException e) {
				logger.error("Failed to get permission by id: {}", permission_id, e);
				throw e;
			}
		}
		return null;
	}

	public Permission getPermissionByName(String permission_name) throws SQLException {
		String sql = "SELECT permission_id, permission_name FROM permissions WHERE permission_name = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, permission_name);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Permission permission = new Permission();
					permission.setPermissionId(rs.getString("permission_id"));
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
