package com.example.expensetrackapp.auth.models;

import java.sql.Timestamp;

public class RefreshToken {
	private String refreshtoken_id;
	private String user_id;
	private String refresh_token;
	private String user_agent;
	private String ip_address;
	private Timestamp expires_at;

	public RefreshToken() {

	}

	public void RefresgToken(String refreshtoken_id, String user_id, String refresh_token, String user_agent, String ip_address, Timestamp expire_at) {
		this.refresh_token = refresh_token;
		this.user_id = user_id;
		this.refresh_token = refresh_token;
		this.user_agent = user_agent;
		this.ip_address = ip_address;
		this.expires_at = expire_at;
	}

	public String getRefreshtoken_id() {
		return refreshtoken_id;
	}

	public void setRefreshtoken_id(String refreshtoken_id) {
		this.refreshtoken_id = refreshtoken_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getIp_address() {
		return ip_address;
	}

	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}

	public Timestamp getExpires_at() {
		return expires_at;
	}

	public void setExpires_at(Timestamp expires_at) {
		this.expires_at = expires_at;
	}

	
}
