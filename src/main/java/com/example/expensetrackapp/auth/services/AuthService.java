package com.example.expensetrackapp.auth.services;

import com.example.expensetrackapp.auth.dao.UserDAO;

import com.example.expensetrackapp.auth.models.User;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
	private UserDAO userDao;
	private JwtService jwtService;

	public AuthService() {
		this.userDao = new UserDAO();
		this.jwtService = new JwtService();
	}

	public boolean registerUser(String username, String password, String email) {
		User userExist = userDao.getUsersByUsername(username);
		if (userExist != null) {
			logger.warn("Registation failed: Username '{}' already exists", username);
		}

		// Hash password by bcrypt
		String salt = BCrypt.gensalt();
		String hashPassword = BCrypt.hashpw(password, salt);

		User newUser = new User();
		newUser.setUsername(username);
		newUser.setPassword(hashPassword);
		newUser.setEmail(email);

		boolean success = userDao.createUser(newUser);
		if (success) {
			logger.info("User '{}' registerd successfully", username);
		} else {
			logger.error("Fail creating account");
		}

		return success;
	}

	public String loginUser(String username, String password) {

		User userExist = userDao.getUsersByUsername(username);
		if (userExist == null) {
			logger.warn("Login failed: Username '{}' not registerd", username);
		}

		logger.info("Get password login '{}'", userExist);

		// Compare password hashed
		if ((BCrypt.checkpw(password, userExist.getPassword()))) {
			logger.info("User '{}' login successfully");
			return jwtService.generateToken(username, userExist.getRole());
		} else {
			logger.warn("Login failed: Invalid password for user '{}'", username);
			return null;
		}
	}

}