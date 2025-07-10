package com.example.expensetrackapp.auth.models;

import java.sql.Timestamp;

public class User {
	private String user_id;
	private String username;
	private String password;
	private String email;
	private String role;
	private Timestamp createdAt;

	public User() {
	}

	public User(String user_id, String username, String password, String email, String role, Timestamp createdAt) {
		this.user_id = user_id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.createdAt = createdAt;
	}

	public void setUserId(String user_id) {
		this.user_id = user_id;
	}

	public String getUserId() {
		return user_id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}