package com.example.expensetrackapp.auth.models;

import java.util.UUID;

public class UserRequest {
	private String username;
	private String email;
	private UUID group_id;
	private UUID user_id;
	private String role_name;
	private UUID role_id;
	
	public UserRequest() {
		
	}
	
	public UserRequest(String username, String email, UUID group_id, UUID user_id, String role_name) {
		this.username = username;
		this.email = email;
		this.group_id = group_id;
		this.user_id = user_id;
		this.role_name = role_name;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public UUID getGroup_id() {
		return group_id;
	}

	public void setGroup_id(UUID group_id) {
		this.group_id = group_id;
	}

	public UUID getUser_id() {
		return user_id;
	}

	public void setUser_id(UUID user_id) {
		this.user_id = user_id;
	}
}
