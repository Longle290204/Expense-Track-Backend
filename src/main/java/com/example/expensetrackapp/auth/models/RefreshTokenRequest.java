package com.example.expensetrackapp.auth.models;

import java.sql.Timestamp;

public class RefreshTokenRequest {
	private String refreshToken;
    private String username; // có thể bỏ nếu không dùng

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
