package com.example.expensetrackapp.auth.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.config.DBConnection;
import com.example.expensetrackapp.auth.models.User;

public class UserDAO {
	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

	public User getUsersByUsername(String username) {

		String sql = "SELECT user_id, username, password, email, role, createdAt FROM users WHERE username = ?";
		Connection connect = null;
		logger.info("SQL Query being executed: '{}'", sql); // Thêm dòng này
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		User user = null;

		try {
			connect = DBConnection.getConnection();
			pstmt = connect.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				user = new User();

				user.setUserId(rs.getString("user_id"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setCreatedAt(rs.getTimestamp("createdAt"));
			}
		} catch (SQLException e) {
			logger.error("Error retrieving user by username: " + username, e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (connect != null)
					connect.close();
			} catch (SQLException e) {
				logger.error("Failed to close resources in getUserByUsername", e);
			}
		}

		return user;
	}

	public User getUsersByEmail(String email) {
		String sql = "SELECT user_id, username, password, email, role, createdAt FROM users WHERE email = ?";

		Connection connect = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		User user = null;

		try {
			connect = DBConnection.getConnection();
			pstmt = connect.prepareStatement(sql);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				user = new User();

				user.setUserId(rs.getString("user_id"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setCreatedAt(rs.getTimestamp("createdAt"));
			}
		} catch (SQLException e) {
			logger.error("Error retrieving user by email: " + email, e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (connect != null)
					connect.close();
			} catch (SQLException e) {
				logger.error("Failed to close resources in getUserByUsername", e);
			}
		}

		return user;
	}

	public boolean createUser(User user) {

		String sql = "INSERT INTO users (username, password, email, role)" + "VALUES (?, ?, ?, ?)";
		Connection connect = null;
		PreparedStatement pstmt = null;

		try {
			logger.error("run this");
			connect = DBConnection.getConnection();
			pstmt = connect.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getRole() != null ? user.getRole() : "USER");
			int rowsUpdated = pstmt.executeUpdate();

			return rowsUpdated > 0;
		} catch (SQLException e) {
			logger.error("Error creating user: " + user.getUsername(), e);
			return false;
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (connect != null)
					connect.close();
			} catch (SQLException e) {
				logger.error("Failed to close resources in createUser", e);
			}
		}
	}
}
