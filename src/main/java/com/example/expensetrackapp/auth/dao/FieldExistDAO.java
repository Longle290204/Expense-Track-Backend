package com.example.expensetrackapp.auth.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.config.DBConnection;

public class FieldExistDAO {
	private static final Logger logger = LoggerFactory.getLogger(FieldExistDAO.class);

	public boolean isUsernameExist(String username) {
		String sql = "SELECT 1 FROM users WHERE username = ?";
		return checkExist(sql, username);
	}

	public boolean isEmailExist(String email) {
		String sql = "SELECT 1 FROM users WHERE email = ?";
		return checkExist(sql, email);
	}

	private boolean checkExist(String sql, String value) {
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, value);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			logger.error("Database error when checking field existence", e);
			return false;
		}
	}
}
