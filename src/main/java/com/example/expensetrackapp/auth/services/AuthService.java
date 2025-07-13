package com.example.expensetrackapp.auth.services;

import com.example.expensetrackapp.auth.dao.UserDAO;
import com.example.expensetrackapp.auth.models.ExistsResponse;
import com.example.expensetrackapp.auth.models.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.dao.FieldExistDAO;
import com.example.expensetrackapp.auth.dao.RefreshTokenDAO;

public class AuthService {
	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
	private UserDAO userDao;
	private FieldExistDAO fieldExistDao;
	private JwtService jwtService;

	public AuthService() {
		this.userDao = new UserDAO();
		this.jwtService = new JwtService();
		this.fieldExistDao = new FieldExistDAO();
	}

	public boolean registerUser(String username, String password, String email, String confirmPassword) {
		User userExist = userDao.getUsersByUsername(username);
		if (userExist != null) {
			logger.warn("Registation failed: Username '{}' already exists", username);
			return false;
		}

		User emailExist = userDao.getUsersByEmail(email);
		if (emailExist != null) {
			logger.warn("Registation failed: Email '{}' already exists", email);
			return false;
		}
		
		logger.info("logger confirmPassword '{}'", confirmPassword);
		logger.info("logger Password '{}'", password);
		if (confirmPassword != null && confirmPassword.equals(password)) {
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
		
		return false;
	}

	public String[] loginUser(String username, String password, String userAgent, String ipAddress)
			throws SQLException {

		User userExist = userDao.getUsersByUsername(username);
		if (userExist == null) {
			logger.warn("Login failed: Username '{}' not registerd", username);
		}

		logger.info("Get password login '{}'", userExist);

		// Compare password hashed
		if ((BCrypt.checkpw(password, userExist.getPassword()))) {
			logger.info("User '{}' login successfully");

			long expiresAtLong = JwtService.EXPIRATION_TIME_REFRESH;
			Timestamp expiresAt = new Timestamp(expiresAtLong);

			String accessToken = jwtService.generateAccessToken(username, password);
			String refreshToken = jwtService.generateRefreshToken(username, accessToken);

			// Update refresh token table
			String userIdStr = userExist.getUserId(); // trả về String
			UUID userId = UUID.fromString(userIdStr);
			try {
				RefreshTokenDAO.saveRefreshToken(userId, refreshToken, userAgent, ipAddress, expiresAt);
			} catch (SQLException e) {
				logger.error("Fail to save refresh token table", e);
			}

			return new String[] { accessToken, refreshToken };
		} else {
			logger.warn("Login failed: Invalid password for user '{}'", username);
			return null;
		}
	}

	public ExistsResponse existsRespone(String field, String value) throws SQLException {

		boolean exists = false;
		if (field.equals("username")) {
			exists = fieldExistDao.isUsernameExist(value);
		} else if (field.equals("email")) {
			exists = fieldExistDao.isEmailExist(value);
		}

		return new ExistsResponse(exists);
	}
}