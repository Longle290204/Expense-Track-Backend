package com.example.expensetrackapp.group.model;

public class GroupRequest {
	private String group_name;

	public GroupRequest() {

	}

	public GroupRequest(String group_name) {
		this.group_name = group_name;
		
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

}
