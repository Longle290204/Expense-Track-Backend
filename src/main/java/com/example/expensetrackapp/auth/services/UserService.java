package com.example.expensetrackapp.auth.services;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.api.BaseApiServlet;
import com.example.expensetrackapp.auth.api.RoleApiServlet;
import com.example.expensetrackapp.auth.dao.UserDAO;
import com.example.expensetrackapp.auth.models.User;

public class UserService extends BaseApiServlet {
	private static final Logger logger = LoggerFactory.getLogger(RoleApiServlet.class);
	private UserDAO userDao;

	public UserService() {
		userDao = new UserDAO();
	}

	public void addUserToGroupService(UUID group_id, String email) {
		try {
			boolean isAddSuccess = userDao.addUserToGroupDao(group_id, email);

			if (!isAddSuccess) {
				throw new RuntimeException("Can't add user to group cause logic code");
			}

		} catch (SQLException e) {
			logger.error("Error when add user to group", e);

			throw new RuntimeException("System error when add user.");
		}
	}

	/**
	 * 
	 * @param username
	 * @param email
	 * @param group_id
	 * @return
	 */
	public User getUserWithRolAndPermisService(String username, String email, UUID group_id) {
		try {
			User user = userDao.getUserWithRolesAndPermissionsDao(username, email, group_id);
			logger.info("return user {}", user);
			if (user == null) {
				throw new RuntimeException("Can't get user with roles and permissions to group cause logic code");
			}

			return user;
		} catch (SQLException e) {
			logger.error("Error when get user roles and permissions to group", e);
			throw new RuntimeException("System error when add user.");
		}
	}

	public List<User> getAllUsersWithRolAndPer(UUID group_id) {
		try {
			List<User> users = userDao.getAllUsersWithRolAndPer(group_id);

			logger.info("Returned users with roles and permissions: {}", users);
			return users;
		} catch (SQLException e) {
			logger.error("Error when getting user roles and permissions for group {}", group_id, e);
			throw new RuntimeException("System error when getting users.");
		}
	}

}
