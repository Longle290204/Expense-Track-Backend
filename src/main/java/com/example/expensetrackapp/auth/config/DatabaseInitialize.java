package com.example.expensetrackapp.auth.config;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// User @WebListener to Tomcat auto indentify
@WebListener
public class DatabaseInitialize implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseInitialize.class);

	// This function is auto call when application run
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Connection connect = null;
		PreparedStatement pstmt = null;

		try {
			connect = DBConnection.getConnection();

			pstmt = connect.prepareStatement("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";");

			// SQL table user
			String createTableCommand = "CREATE TABLE IF NOT EXISTS users" + "("
					+ "user_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY," + "username VARCHAR(255) NOT NULL,"
					+ "email VARCHAR(255) NOT NULL," + "role VARCHAR(255) DEFAULT 'USER'," + "password VARCHAR(255),"
					+ "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";
			pstmt = connect.prepareStatement(createTableCommand);
			pstmt.executeUpdate();
			logger.info("Table 'users' created.");

			// SQL refresh token table
			String createRefreschTokenCmd = "CREATE TABLE IF NOT EXISTS refresh_tokens " + "("
					+ "refreshtoken_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, " + "user_id uuid NOT NULL, "
					+ "refresh_token TEXT, " + "user_agent TEXT, " + "ip_address TEXT, "
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + "expires_at TIMESTAMP NOT NULL, "
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" + ")";
			pstmt = connect.prepareStatement(createRefreschTokenCmd);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error during database initialization" + e.getMessage(), e);
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (connect != null)
					connect.close();
			} catch (SQLException ex) {
				logger.error("Failed to close resources in DatabaseInitializer", ex);
			}
		}
	}
}
