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
			String createTableUserCmd = "CREATE TABLE IF NOT EXISTS users" + "("
					+ "user_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY," + "username VARCHAR(255) NOT NULL,"
					+ "email VARCHAR(255) NOT NULL, " + "password VARCHAR(255),"
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
					+ "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";
			pstmt = connect.prepareStatement(createTableUserCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'users' created.");

			// SQL table group
			String createTableGroupCmd = "CREATE TABLE IF NOT EXISTS groups (group_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, "
					+ "group_name VARCHAR(50) NOT NULL, " + "created_by uuid NOT NULL, "
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";
			pstmt = connect.prepareStatement(createTableGroupCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'groups' created.");

			String createTableUserGroupCmd = "CREATE TABLE IF NOT EXISTS user_groups (" + "group_id UUID NOT NULL, "
					+ "user_id UUID NOT NULL, " + "joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
					+ "email VARCHAR(50) NOT NULL, " + "PRIMARY KEY (group_id, user_id), "
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id), "
					+ "FOREIGN KEY (group_id) REFERENCES groups(group_id)" + ")";
			pstmt = connect.prepareStatement(createTableUserGroupCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'user_groups' created.");

			String createTableCategoriesCmd = "CREATE TABLE IF NOT EXISTS categories ("
					+ "    category_id UUID PRIMARY KEY, " + "    name VARCHAR(20) NOT NULL, "
					+ "    type VARCHAR(10) NOT NULL, " + "    user_id UUID, "
					+ "    FOREIGN KEY (user_id) REFERENCES users(user_id)" + ")";
			pstmt = connect.prepareStatement(createTableCategoriesCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'categories' created.");

			String createTableBudgetCmd = "CREATE TABLE IF NOT EXISTS budgets ("
					+ "budget_id UUID DEFAULT uuid_generate_v4(), " + "user_id UUID NOT NULL, " + "wallet_id UUID, "
					+ "group_id UUID, " + "category_id UUID, " + "amount DECIMAL(10, 2) NOT NULL, "
					+ "start_date DATE NOT NULL, " + "end_date DATE NOT NULL, "
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + "PRIMARY KEY (budget_id), "
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id), "
					+ "FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id), "
					+ "FOREIGN KEY (group_id) REFERENCES groups(group_id), "
					+ "FOREIGN KEY (category_id) REFERENCES categories(category_id)" + ")";
			pstmt = connect.prepareStatement(createTableBudgetCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'budget' created.");

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
			String createRoleTableCmd = "CREATE TABLE IF NOT EXISTS roles ("
					+ "role_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, " + "role_name varchar(50) NOT NULL, "
					+ "is_system BOOLEAN, " + "group_id uuid, " + "UNIQUE (group_id, role_name)" + ")";
			pstmt = connect.prepareStatement(createRoleTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'roles' created");

			// SQL permissions table
			String createPermissionTableCmd = "CREATE TABLE IF NOT EXISTS permissions " + "("
					+ "permission_id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, "
					+ "permission_name varchar(50) NOT NULL UNIQUE, " + "group_name varchar(50) NOT NULL, "
					+ "FOREIGN KEY (group_id) REFERENCES groups(group_id)" + ")";
			pstmt = connect.prepareStatement(createPermissionTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'permissions' created");

			// SQL user_roles table
			String createUserRoleTableCmd = "CREATE TABLE IF NOT EXISTS user_roles (" + "user_id UUID NOT NULL, "
					+ "role_id UUID NOT NULL, " + "group_id UUID NOT NULL, "
					+ "PRIMARY KEY (user_id, role_id, group_id), " + "FOREIGN KEY (user_id) REFERENCES users(user_id), "
					+ "FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (group_id) REFERENCES groups(group_id)" + ")";
			pstmt = connect.prepareStatement(createUserRoleTableCmd);
			pstmt.executeUpdate();
			pstmt.close();
			logger.info("Table 'user_roles' created");

			// SQL role_permisstions table
			String createRolePermissionTableCmd = "CREATE TABLE IF NOT EXISTS role_permissions ("
					+ "role_id UUID NOT NULL, " + "permission_id UUID NOT NULL, " + "group_id UUID, "
					+ "PRIMARY KEY (role_id, permission_id),"
					+ "FOREIGN KEY (role_id) REFERENCES roles(role_id),"
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
