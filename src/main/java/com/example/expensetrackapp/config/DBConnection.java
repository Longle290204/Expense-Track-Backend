package com.example.expensetrackapp.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnection {
	private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

	private static final String url = "jdbc:postgresql://localhost:5433/expense_db";
	private static final String user = "postgres";
	private static final String password = "12345678";

	public static Connection getConnection() throws SQLException {

		try {
			// Lấy ra class driver postgresql JDBC và nạp vào Driver
			Class.forName("org.postgresql.Driver");
			// Connect to the PostgreSQL database
			Connection connect = DriverManager.getConnection(url, user, password);
			logger.info("PostgreSQL database connection established successfully.");
			return connect;
		} catch (ClassNotFoundException e) {
			logger.error("PostgreSQL JDBC Driver not found!", e);
			throw new SQLException("PostgreSQL JDBC Driver not found!", e);
		} catch (SQLException e) {
			logger.error("Database connection failed for PostgreSQL!", e);
			throw e;
		}
	}

	public static void closeConnection(Connection connect) throws SQLException {
		if (connect != null) {
			try {
				connect.close();
				logger.info("Database connection closed.");
			} catch (SQLException e) {
				logger.error("Failed to close database connection", e);
			}
		}
	}
}
