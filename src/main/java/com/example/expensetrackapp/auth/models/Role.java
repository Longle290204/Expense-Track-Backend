package com.example.expensetrackapp.auth.models;

import java.security.Permission;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Role {
	private String role_id;
	private String role_name;
	private Set<Permission> permissions = new HashSet<>();

	public Role() {

	}

	public Role(String role_id, String role_name) {
		this.role_id = role_id;
		this.role_name = role_name;
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

	// equals() và hashCode()
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Role role = (Role) o;
		return role_id == role.role_id && Objects.equals(role_name, role.role_name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(role_id, role_name);
	}

	@Override
	public String toString() {
		return "Role{" + "role_id=" + role_id + ", role_name='" + role_name + '\'' + '}';
	}
}
