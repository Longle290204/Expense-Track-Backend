package com.example.expensetrackapp.auth.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleRequest {
	private UUID user_id;
	private UUID group_id;
	private UUID role_id;
	private String role_name;
	private Boolean is_system;
	private List<UUID> permissionIds = new ArrayList<>();

	public RoleRequest() {

	}


	public RoleRequest(UUID user_id, UUID group_id, UUID role_id, String role_name, Boolean is_system) {
		this.user_id = user_id;
		this.role_id = role_id;
		this.group_id = group_id;
		this.role_name = role_name;
		this.is_system = is_system;
	}
	
	public UUID getUser_id() {
		return user_id;
	}
	
	public void setUser_id(UUID user_id) {
		this.user_id = user_id;
	}

	public UUID getGroup_id() {
		return group_id;
	}

	public void setGroup_id(UUID group_id) {
		this.group_id = group_id;
	}

	public UUID getRole_id() {
		return role_id;
	}

	public void setRole_id(UUID role_id) {
		this.role_id = role_id;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public Boolean getIs_system() {
		return is_system;
	}

	public void setIs_system(Boolean is_system) {
		this.is_system = is_system;
	}

	public List<UUID> getPermissionIds() {
		return permissionIds;
	}

	public void setPermissionIds(List<UUID> permissionIds) {
		this.permissionIds = permissionIds;
	}
}
