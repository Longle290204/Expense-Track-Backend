package com.example.expensetrackapp.auth.models;

public class PermissionRequest {
	private String permission_id;
	private String permission_name;
	private String group_name;

	public PermissionRequest() {

	}

	public PermissionRequest(String permission_id, String permission_name, String group_name) {
		this.permission_id = permission_id;
		this.permission_name = permission_name;
		this.group_name = group_name;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getPermission_id() {
		return permission_id;
	}

	public void setPermission_id(String permission_id) {
		this.permission_id = permission_id;
	}

	public String getPermission_name() {
		return permission_name;
	}

	public void setPermission_name(String psermission_name) {
		this.permission_name = psermission_name;
	}
}
