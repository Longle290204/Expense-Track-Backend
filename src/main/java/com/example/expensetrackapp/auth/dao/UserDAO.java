package com.example.expensetrackapp.auth.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.models.Permission;
import com.example.expensetrackapp.auth.models.Role;
import com.example.expensetrackapp.auth.models.User;
import com.example.expensetrackapp.config.DBConnection;

public class UserDAO {
	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

	/**
	 * 
	 * Get all users in group
	 * 
	 * @param username
	 * @param group_id
	 * @return all users
	 * 
	 */
	public User getUsersByUsername(String username) {

		String sql = "SELECT user_id, username, password, email, created_at FROM users WHERE username = ?";
		User user = null;

		try (Connection connect = DBConnection.getConnection(); PreparedStatement pstmt = connect.prepareStatement(sql);

		) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					user = new User();

					user.setUser_id(rs.getString("user_id"));
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("password"));
					user.setEmail(rs.getString("email"));
					user.setCreatedAt(rs.getTimestamp("created_at"));
				}
			}
		} catch (SQLException e) {
			logger.error("Error retrieving user by username: " + username, e);
		}

		return user;
	}

	/**
	 * Return user by email
	 * 
	 * @param email
	 * @return
	 */
	public User getUsersByEmail(String email) {
		String sql = "SELECT user_id, username, password, email, created_at FROM users WHERE email = ?";

		User user = null;

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					user = new User();

					user.setUser_id(rs.getString("user_id"));
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("password"));
					user.setEmail(rs.getString("email"));
					user.setCreatedAt(rs.getTimestamp("created_at"));
				}
			}
		} catch (SQLException e) {
			logger.error("Error retrieving user by email: " + email, e);
		}

		return user;
	}

	public boolean createUser(User user) {

		String sql = "INSERT INTO users (username, password, email)" + "VALUES (?, ?, ?)";
		Connection connect = null;
		PreparedStatement pstmt = null;

		try {
			logger.error("run this");
			connect = DBConnection.getConnection();
			pstmt = connect.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getEmail());
			int rowsUpdated = pstmt.executeUpdate();

			return rowsUpdated > 0;
		} catch (SQLException e) {
			logger.error("Error creating user: " + user.getUsername(), e);
			return false;
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (connect != null)
					connect.close();
			} catch (SQLException e) {
				logger.error("Failed to close resources in createUser", e);
			}
		}
	}

	/**
	 * 
	 * Tìm người dùng theo username hoặc email bao gồm các vai trò và quyền của họ
	 * trong một group nhất định. Phương thức quan trọng trong quá trình xác thực và
	 * ủy quyền và ủy quyền sau này
	 * 
	 * @param username Tên đăng nhập người dùng
	 * @throws SQLException nếu có lỗi databse
	 * 
	 */
	public User getUserWithRolesAndPermissionsDao(String username, String email, UUID group_id) throws SQLException {
		User user = null;

		String sql = "SELECT u.user_id, u.username, u.email, u.password, u.created_at, u.updated_at, "
				+ "r.role_id, r.role_name, " + "p.permission_id, p.permission_name " + "FROM users u "
				+ "LEFT JOIN user_roles ur ON u.user_id = ur.user_id "
				+ "LEFT JOIN roles r ON ur.role_id = r.role_id AND r.group_id = ? "
				+ "LEFT JOIN role_permissions rp ON r.role_id = rp.role_id "
				+ "LEFT JOIN permissions p ON rp.permission_id = p.permission_id "
				+ "WHERE u.username = ? OR u.email = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setObject(1, group_id);
			pstmt.setString(2, username);
			pstmt.setString(3, email);

			try (ResultSet rs = pstmt.executeQuery()) {
				Set<Role> roles = new HashSet<>();
				Map<String, Role> roleMap = new HashMap<>();

				while (rs.next()) {
					if (user == null) {
						user = new User(rs.getString("user_id"), rs.getString("username"), rs.getString("password"),
								rs.getString("email"));
						user.setCreatedAt(rs.getTimestamp("created_at"));
						user.setUpdatedAt(rs.getTimestamp("updated_at"));
					}

					String roleId = rs.getString("role_id");
					if (roleId != null) {
						Role role = roleMap.get(roleId);
						if (role == null) {
							role = new Role(roleId, "ADMIN");
							roleMap.put(roleId, role);
							roles.add(role);
						}

						UUID permId = UUID.fromString(rs.getString("permission_id"));
						String permName = rs.getString("permission_name");
						if (permId != null) {
							role.getPermissions().add(new Permission(permId, permName));
						}
					}
				}
				
				if (user != null) {
					user.setRoles(roles);
				}
			}
		}

		return user;
	}

	/**
	 * 
	 * @return Trả về ArrayList chứa tất cả user
	 * @throws SQLException
	 * 
	 */
	public List<User> getAllUsers() throws SQLException {
		List<User> users = new ArrayList<>();
		String sql = "SELECT user_id, username, email, created_at, updated_at FROM Users ORDER BY username";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					User user = new User(rs.getString("user_id"), rs.getString("username"), rs.getString("email"),
							null);
					user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at") : null);
					user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at") : null);
					users.add(user);
				}
			}
		}

		return users;
	}

	/**
	 * Lấy một người dùng theo ID, bao gồm các vai trò của họ
	 * 
	 * @param user_id ID người dùng
	 * @return trả về user nếu được tìm thấy hoặc null
	 * @throws SQLException
	 */
	public User getUserById(String user_id) throws SQLException {
		User user = null;

		String sql = "SELECT u.user_id, u.user_name, u.email, u.password, u.createdAt" + "FROM user u "
				+ "JOIN user_roles ur ON u.user_id = ur.user_id " + "JOIN roles r ON ur.role_id = r.role_id "
				+ "WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, user_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				Set<Role> roles = new HashSet<>();
				while (rs.next()) {
					if (user == null) {
						user = new User(rs.getString("user_id"), rs.getString("username"), rs.getString("email"),
								rs.getString("password"));
						user.setCreatedAt(rs.getTimestamp("createdat") != null ? rs.getTimestamp("createdat") : null);
						user.setUpdatedAt(rs.getTimestamp("updatedat") != null ? rs.getTimestamp("updatedat") : null);
					}

					String roleId = rs.getString("role_id");
					String roleName = rs.getString("role_name");
					if (roleId != null && roleName != null) {
						roles.add(new Role(roleId, roleName));
					}
				}

				if (user != null) {
					user.setRoles(roles);
				}
			}
		}

		return user;
	}

	/**
	 * Cập nhật thông tin cơ bản của người dùng
	 * 
	 * @param user Đối tượng User với thông tin cần cập nhật
	 * @return true nếu cập nhật thành công, false nếu không tìm thầy người dùng
	 * @throws SQLException
	 */
	public boolean updateUser(String username, String email, String password, Timestamp updatedat) throws SQLException {
		String sql = "UPDATE users SET username = ?, email = ?, password = ?, updatedat = ? WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.setString(2, email);
			pstmt.setString(3, password);
			pstmt.setTimestamp(4, updatedat);
			return pstmt.executeUpdate() > 0;
		}
	}

	/**
	 * 
	 * @param user_id         ID người dùng
	 * @param newPasswordHash mật khẩu mới người dùng
	 * @return true nếu cập nhật thành công
	 * @throws SQLException Trả về lỗi nếu lỗi db
	 * 
	 */
	public boolean updatePassword(String user_id, String newPasswordHash) throws SQLException {
		String sql = "UPDATE users SET password = ?, updatedat = ? WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, newPasswordHash);
			pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setString(3, user_id);
			return pstmt.executeUpdate() > 0;
		}
	}

	/**
	 * Xóa một người dùng khỏi DB. Lưu ý: Các liên kết trong User_Roles sẽ tự động
	 * bị xóa do ON DELETE CASCADE.
	 * 
	 * @param userId ID của người dùng cần xóa
	 * @return true nếu xóa thành công, false nếu không tìm thấy người dùng
	 * @throws SQLException Nếu có lỗi khi truy vấn DB
	 */
	public boolean deleteUser(int userId) throws SQLException {
		String sql = "DELETE FROM Users WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			return pstmt.executeUpdate() > 0;
		}
	}

	/**
	 * 
	 * @param user_id
	 * @param roleIds
	 * @throws SQLException
	 */
	public void removeRolesFromUser(String user_id, List<String> roleIds) throws SQLException {
		String sql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {

			pstmt.setString(1, user_id);
			for (String roleId : roleIds) {
				pstmt.setString(2, roleId);
				pstmt.addBatch();
			}

			pstmt.executeBatch();
		}
	}

	public boolean addUserToGroupDao(UUID group_id, UUID user_id) throws SQLException {
		String sql = "INSERT INTO user_groups (group_id, user_id) VALUES (?, ?)";
		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setObject(1, group_id);
			pstmt.setObject(2, user_id);

			int rowUpdate = pstmt.executeUpdate();
			return rowUpdate > 0;
		}
	}
}
