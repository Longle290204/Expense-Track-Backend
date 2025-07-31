package com.example.expensetrackapp.auth.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Role {
	private String role_id;
	private String role_name;
	private Set<Permission> permissions = new HashSet<>();
	private Boolean is_system;
	private UUID group_id;
	private UUID userId;

	public Role() {

	}

	public Role(String role_id, String role_name, UUID group_id, Boolean is_system) {
		this.role_id = role_id;
		this.role_name = role_name;
		this.group_id = group_id;
		this.is_system = is_system;

	}

	public UUID getGroup_id() {
		return group_id;
	}

	public void setGroup_id(UUID group_id) {
		this.group_id = group_id;
	}

	public String getRoleId() {
		return role_id;
	}

	public void setRoleId(String role_id) {
		this.role_id = role_id;
	}

	public String getRoleName() {
		return role_name;
	}

	public void setRoleName(String role_name) {
		this.role_name = role_name;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	@Override
	public int hashCode() {
		return Objects.hash(role_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		return Objects.equals(role_id, other.role_id);
	}

	@Override
	public String toString() {
		return "Role{" + "role_id=" + role_id + ", role_name='" + role_name + '\'' + '}';
	}

	public Boolean getIsSystemRole() {
		return is_system;
	}

	public void setIsSystemRole(Boolean is_system) {
		this.is_system = is_system;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getUserId() {
		return userId;
	}
}
