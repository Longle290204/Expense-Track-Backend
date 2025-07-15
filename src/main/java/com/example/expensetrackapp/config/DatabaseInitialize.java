package com.example.expensetrackapp.config;

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
			pstmt.close();
			logger.info("Table 'users' created.");

			// SQL refresh token table
			String createRefreschTokenCmd = "CREATE TABLE IF NOT EXISTS refresh_tokens " + "("
					+ "refreshtoken_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, " + "user_id uuid NOT NULL, "
					+ "refresh_token TEXT, " + "user_agent TEXT, " + "ip_address TEXT, "
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + "expires_at TIMESTAMP NOT NULL, "
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" + ")";
			pstmt = connect.prepareStatement(createRefreschTokenCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'refresh tokens' created");

			// SQL transaction table
			String createTransactionTableCmd = "CREATE TABLE IF NOT EXISTS transactions " + "("
					+ "transaction_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, " + "user_id uuid NOT NULL, "
					+ "group_id uuid, " + "wallet_id uuid, " + "category_id uuid, " + "title VARCHAR(255) NOT NULL, "
					+ "amount DECIMAL(10, 2) NOT NULL, " + "type VARCHAR(255) NOT NULL, " + "date DATE NOT NULL, "
					+ "note TEXT, " + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";
			pstmt = connect.prepareStatement(createTransactionTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'transactions' created");

			// SQL wallet table
			String createWalletTableCmd = "CREATE TABLE IF NOT EXISTS wallets " + "("
					+ "wallet_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, " + "name varchar(100) NOT NULL, "
					+ "user_id uuid NOT NULL, " + "group_id uuid, " + "currency varchar(10) DEFAULT 'VND', "
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP " + ")";
			pstmt = connect.prepareStatement(createWalletTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'wallets' created");

			// SQL role table
			String createRoleTableCmd = "CREATE TABLE IF NOT EXISTS roles " + "("
					+ "role_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, " + "name varchar(50) NOT NULL UNIQUE "
					+ ")";
			pstmt = connect.prepareStatement(createRoleTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'roles' created");

			// SQL permissions table
			String createPermissionTableCmd = "CREATE TABLE IF NOT EXISTS permissions " + "("
					+ "permission_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, "
					+ "name varchar(50) NOT NULL UNIQUE" + ")";
			pstmt = connect.prepareStatement(createPermissionTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'permissions' created");

			// SQL user_roles table
			String createUserRoleTableCmd = "CREATE TABLE IF NOT EXISTS user_roles " + "(" + "user_id UUID NOT NULL, "
					+ "role_id UUID NOT NULL, " + "PRIMARY KEY (user_id, role_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (role_id) REFERENCES roles(role_id)" + ")";
			pstmt = connect.prepareStatement(createUserRoleTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'user_roles' created");

			// SQL role_permisstions table
			String createRolePermissionTableCmd = "CREATE TABLE IF NOT EXISTS role_permissions " + "("
					+ "role_id UUID NOT NULL, " + "permission_id UUID NOT NULL, "
					+ "PRIMARY KEY (role_id, permission_id)," + "FOREIGN KEY (role_id) REFERENCES roles(role_id),"
					+ "FOREIGN KEY (permission_id) REFERENCES permissions(permission_id)" + ")";
			pstmt = connect.prepareStatement(createRolePermissionTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'role_permissions' created");

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
