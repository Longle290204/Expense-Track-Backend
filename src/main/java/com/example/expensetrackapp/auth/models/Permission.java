package com.example.expensetrackapp.auth.models;

import java.util.Objects;
import java.util.UUID;

public class Permission {
	private UUID permission_id;
	private String permission_name;

	public Permission() {
	}

	public Permission(UUID permission_id, String permission_name) {
		this.permission_id = permission_id;
		this.permission_name = permission_name;
	}

	public UUID getPermissionId() {
		return permission_id;
	}

	public void setPermissionId(UUID permission_id) {
		this.permission_id = permission_id;
	}

	public String getPermissionName() {
		return permission_name;
	}

	public void setPermissionName(String permission_name) {
		this.permission_name = permission_name;
	}

	@Override
	public boolean equals(Object o) {
		  if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        Permission that = (Permission) o;
	        return permission_id == that.permission_id &&
	               Objects.equals(permission_name, that.permission_name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(permission_id, permission_name);
	}

	@Override
	public String toString() {
		return "Permission{" + "permissionId=" + permission_id + ", permission_name='" + permission_name + '\'' + '}';
	}

}
