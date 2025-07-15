package com.example.expensetrackapp.auth.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.dao.RoleDAO;
import com.example.expensetrackapp.auth.models.Role;

public class AuthorizationService {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
	
	private RoleDAO roleDao;
	
	public AuthorizationService() {
		this.roleDao = new RoleDAO();
	}
 
	public boolean addRole(String name) {
		
		Role roleExist = roleDao.getRoleDAO(name);
		if (roleExist != null) {
			logger.warn("Add role failed: Role '{}' already exists", name);
			return false;
		}
		
		boolean success = roleDao.createRoleDAO(name);
		
		if (success) {
			logger.info("Role '{}' created successfully", name);
			return true;
		} else {
			logger.error("Failed to creating role");
		}
		
		return false;
	}
}
