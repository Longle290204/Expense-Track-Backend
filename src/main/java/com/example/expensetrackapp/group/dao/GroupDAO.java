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
import com.example.expensetrackapp.auth.models.User;
import com.example.expensetrackapp.config.DBConnection;

public class GroupDAO {

	private static final Logger logger = LoggerFactory.getLogger(GroupDAO.class);

	public boolean createGroupDao(String group_name, String user_id) throws SQLException {
		logger.info("group_name: {}, user_id: {}", group_name, user_id);
		String sql = "INSERT INTO groups (group_name, created_by) VALUES (?, ?::uuid)";

		try (Connection connect = DBConnection.getConnection();
				PreparedStatement pstmt = connect.prepareStatement(sql)) {
			pstmt.setString(1, group_name);
			pstmt.setString(2, user_id);

			int rowsUpdate = 0;
			try {
				rowsUpdate = pstmt.executeUpdate();
			} catch (SQLException e) {
				logger.error("SQLException when executing INSERT: {}", e.getMessage(), e);
				throw e; // hoặc return false nếu bạn không muốn ném ra ngoài
			}

			if (rowsUpdate == 0) {
				logger.warn("INSERT failed: No row inserted for group_name={} and user_id={}", group_name, user_id);
			}

			logger.info("run this create group dao {}", rowsUpdate);
			return rowsUpdate > 0;

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
}
