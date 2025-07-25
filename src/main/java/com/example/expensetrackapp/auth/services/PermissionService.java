package com.example.expensetrackapp.auth.services;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.dao.PermissionDAO;

public class PermissionService {
	private PermissionDAO permissionDao = null;

	public PermissionService() {
		permissionDao = new PermissionDAO();
	}

	private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
	
	public void addPermission(String permission_name, String group_name) {
		try {
			boolean addSuccess = permissionDao.addPermission(permission_name, group_name);

			if (!addSuccess) {
				throw new RuntimeException("Can't add permission to role");
			}
		} catch (SQLException e) {
			logger.error("Error when add permission", e);
			throw new RuntimeException("Error system when add permission to role");
		}
	}
}
