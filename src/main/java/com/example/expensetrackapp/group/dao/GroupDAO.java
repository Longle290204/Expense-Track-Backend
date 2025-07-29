package com.example.expensetrackapp.group.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.models.Group;
import com.example.expensetrackapp.auth.models.Role;
import com.example.expensetrackapp.auth.models.User;
import com.example.expensetrackapp.config.DBConnection;

public class GroupDAO {

	private static final Logger logger = LoggerFactory.getLogger(GroupDAO.class);

	public UUID createGroupDao(String group_name, String user_id) throws SQLException {
		logger.info("group_name: {}, user_id: {}", group_name, user_id);
		String sql = "INSERT INTO groups (group_name, created_by) VALUES (?, ?::uuid) RETURNING group_id";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {

			pstmt.setString(1, group_name);
			pstmt.setString(2, user_id);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					UUID groupId = (UUID) rs.getObject("group_id");
					logger.info("Created group with group_id={}", groupId);
					return groupId;
				} else {
					throw new SQLException("Failed to create group, no ID returned.");
				}
			}
		}
	}

	public int cloneSystemRolesToGroupDao(UUID targetGroupId) throws SQLException {

		String sql = "INSERT INTO roles (role_id, group_id, role_name, is_system) "
				+ "SELECT uuid_generate_v4(), ?, role_name, false FROM roles WHERE is_system = true";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setObject(1, targetGroupId);
			int rowsInserted = stmt.executeUpdate();
			return rowsInserted;
		} catch (SQLException e) {
			logger.error("cloneSystemRolesToGroupDao failed. SQLState={}, ErrorCode={}, Message={}", e.getSQLState(),
					e.getErrorCode(), e.getMessage(), e);
			throw new RuntimeException(e); // hoặc ném tiếp SQLException
		}
	}

	public Group getGroupById(String group_id) throws SQLException {

		String sql = "SELECT group_id, group_name, created_by FROM groups WHERE group_id = ?";
		Group group = null;

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setString(1, group_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					group = new Group();

					group.setGroup_id(rs.getString("group_id"));
					group.setGroup_name(rs.getString("group_name"));
					group.setCreatedBy(UUID.fromString(rs.getString("created_by")));
				}
			} catch (SQLException e) {
				logger.error("Faild to get group by id");
			}
		}

		return group;
	}

	public void removeGroupById(UUID group_id) throws SQLException {
		String sql = "DELETE FROM groups WHERE group_id = ?";
		logger.info("group id {}", group_id);

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {

			pstmt.setObject(1, group_id);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected == 0) {
				logger.warn("No group found with id: " + group_id);
				throw new RuntimeException("No group found with the provided ID");
			} else {
				logger.info("Deleted " + rowsAffected + " group(s) with id: " + group_id);
			}

		} catch (SQLException e) {
			logger.error("Failed to remove group by id", e);
			throw e; // Rethrow để báo lỗi lên tầng trên
		}
	}

	public Group getGroupByGroupName(String group_name) throws SQLException {

		String sql = "SELECT group_id, group_name, created_by FROM groups WHERE group_name = ?";
		Group group = null;

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setString(1, group_name);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					group = new Group();

					group.setGroup_id(rs.getString("group_id"));
					group.setGroup_name(rs.getString("group_name"));
					group.setCreatedBy(UUID.fromString(rs.getString("created_by")));
				}
			} catch (SQLException e) {
				logger.error("Faild to get group by id");
			}
		}
		return group;
	}

	public User getUserInGroupByIdDao(UUID user_id, UUID group_id) throws SQLException {

		User user = null;

		String sql = "SELECT u.user_id, u.username, u.email, u.password " + "FROM users u "
				+ "JOIN user_groups ug ON ug.user_id = u.user_id " + "WHERE u.user_id = ? AND ug.group_id = ?";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setObject(1, user_id);
			pstmt.setObject(2, group_id);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				user = new User(rs.getString("user_id"), rs.getString("username"), rs.getString("email"),
						rs.getString("password"));
			}
		}

		return user;
	}

//	public Set<User> getAllUserInGroupDao() {
//		return 
//	}

	public List<Group> getAllGroupDao() throws SQLException {
		String sql = "SELECT group_id, group_name, created_by, created_at FROM groups";

		List<Group> groups = new ArrayList<>();

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Group group = new Group(rs.getString("group_id"), rs.getString("group_name"));
				group.setCreatedBy(UUID.fromString(rs.getString("created_by")));
				group.setCreatedAt(rs.getTimestamp("created_at"));

				groups.add(group);
			}

		}
		return groups;
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Group> getAllGroupUserIdDao(UUID user_id) throws SQLException {
		String sql = "SELECT g.group_id, g.group_name, g.created_by, g.created_at " + "FROM groups g "
				+ "WHERE g.created_by = ?";

		List<Group> groups = new ArrayList<>();

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setObject(1, user_id);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Group group = new Group(rs.getString("group_id"), rs.getString("group_name"));
				group.setCreatedBy(UUID.fromString(rs.getString("created_by")));
				group.setCreatedAt(rs.getTimestamp("created_at"));

				groups.add(group);
			}

		}
		return groups;
	}

	public void updateGroupNameDao(String group_name, UUID group_id) throws SQLException {
		logger.info("group_name {}, group_id {}", group_name, group_id);
		String sql = "UPDATE groups SET group_name = ? WHERE group_id = ?";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setString(1, group_name);
			pstmt.setObject(2, group_id);

			pstmt.executeUpdate();

		}
	}
}
