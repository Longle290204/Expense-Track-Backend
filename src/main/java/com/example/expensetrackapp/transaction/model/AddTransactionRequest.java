package com.example.expensetrackapp.transaction.model;

import java.util.Date;

public class AddTransactionRequest {
	private String user_id;
	private String title;
	private double amount;
	private String type;
	private Date date;
	private String wallet_id;
	private String category_id;
	private String note;

	public AddTransactionRequest() {

	}

	public AddTransactionRequest(String transaction_id, String user_id, String title, double amount, String type,
			Date date, String note) {
		this.user_id = user_id;
		this.title = title;
		this.amount = amount;
		this.type = type;
		this.date = date;
		this.note = note;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWalletId() {
		return wallet_id;
	}

	public void setWalletId(String wallet_id) {
		this.wallet_id = wallet_id;
	}

	public void setCategoryId(String category_id) {
		this.category_id = category_id;
	}

	public String getCategoryId() {
		return category_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
