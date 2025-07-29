package com.example.expensetrackapp.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.models.RefreshTokenRecord;
import com.example.expensetrackapp.config.DBConnection;

public class RefreshTokenDAO {
	private static final Logger logger = LoggerFactory.getLogger(RefreshTokenDAO.class);

	public void saveRefreshToken(UUID userId, String token, LocalDateTime expiresAt) throws SQLException {
	    String sql = "INSERT INTO refresh_tokens (user_id, refresh_token, expires_at) VALUES (?, ?, ?)";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setObject(1, userId);
	        pstmt.setString(2, token);
	        pstmt.setTimestamp(3, Timestamp.valueOf(expiresAt));
	        int rows = pstmt.executeUpdate();
	        logger.info("[DAO] saved refreshToken rows={}", rows);
	    }
	}


	public Optional<RefreshTokenRecord> findByToken(String refreshToken) throws SQLException {
		
		String sql = "SELECT refreshtoken_id, user_id, refresh_token, created_at, expires_at FROM refresh_tokens WHERE refresh_token = ?";
		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setString(1, refreshToken.trim());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					RefreshTokenRecord r = new RefreshTokenRecord();
					r.setId((UUID) rs.getObject("refreshtoken_id"));
					r.setUserId((UUID) rs.getObject("user_id"));
					r.setToken(rs.getString("refresh_token"));
					r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
					r.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
					return Optional.of(r);
				}
			}
		}

		return Optional.empty();
	}

	public void rotateRefreshToken(UUID recordId, String newToken, LocalDateTime newExpiresAt) throws SQLException {
	    String sql = "UPDATE refresh_tokens SET refresh_token = ?, created_at = CURRENT_TIMESTAMP, expires_at = ? WHERE refreshtoken_id = ?";

	    try (Connection connect = DBConnection.getConnection();
	         PreparedStatement pstmt = connect.prepareStatement(sql)) {
	        pstmt.setString(1, newToken);
	        pstmt.setTimestamp(2, Timestamp.valueOf(newExpiresAt));
	        pstmt.setObject(3, recordId);
	        pstmt.executeUpdate();
	    }
	}

	
	   // Nếu muốn revoke khi lỗi/đăng xuất:
    public void deleteByToken(String refreshToken) throws SQLException {
        String sql = "DELETE FROM refresh_tokens WHERE refresh_token = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, refreshToken);
            ps.executeUpdate();
        }
    }
}
