package com.example.expensetrackapp.auth.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Group {
	private String group_id;
	private String group_name;
	private UUID createdBy;
	private Timestamp createdAt;

	public Group() {

	}

	public Group(String group_id, String group_name) {
		this.group_id = group_id;
		this.group_name = group_name;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public UUID getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UUID createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

}
