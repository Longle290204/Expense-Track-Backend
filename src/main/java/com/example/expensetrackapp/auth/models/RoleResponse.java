package com.example.expensetrackapp.auth.models;

public class RoleResponse {
	private String name;

	public RoleResponse() {
	}
	
	public RoleResponse(String name) {
		this.name = name;
	}
	
	public String getRoleName() {
		return name;
	}
	
	public void setRoleName(String name)  {
		this.name = name;
	}
}
