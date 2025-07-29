package com.example.expensetrackapp.auth.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshTokenRecord {
	private UUID id;
	private UUID userId;
	private String token;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;
	
	public RefreshTokenRecord() {
		
	}
	
	public RefreshTokenRecord(UUID id, UUID userId, String token, LocalDateTime createdAt, LocalDateTime expiresAt) {
		this.id = id;
		this.userId = userId;
		this.token = token;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	
	
}
