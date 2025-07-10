package com.example.expensetrackapp.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.config.DBConnection;

public class RefreshTokenDAO {
	private static final Logger logger = LoggerFactory.getLogger(RefreshTokenDAO.class);

	public static void saveRefreshToken(UUID user_id, String refresh_token, String user_agent, String ip_address,
			Timestamp expires_at) throws SQLException {
		String sql = "INSERT INTO refresh_tokens (user_id, refresh_token, user_agent, ip_address, expires_at)"
				+ "VALUES (?, ?, ?, ?, ?)";
		Connection connect = null;
		PreparedStatement pstmt = null;

		try {
			connect = DBConnection.getConnection();
			pstmt = connect.prepareStatement(sql);
			pstmt.setObject(1, user_id);
			pstmt.setString(2, refresh_token);
			pstmt.setString(3, user_agent);
			pstmt.setString(4, ip_address);
			pstmt.setTimestamp(5, expires_at);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error updating refresh token table", e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close resources in saveRefreshToken", e);
			}

		}
	}
}
