package com.example.expensetrackapp.group.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.models.Group;
import com.example.expensetrackapp.group.dao.GroupDAO;

public class GroupService {
	private static final Logger logger = LoggerFactory.getLogger(GroupDAO.class);

	private GroupDAO groupDao;

	public GroupService() {
		this.groupDao = new GroupDAO();
	}

	/**
	 * Create a new group with the specified information if it does not already
	 * exist.
	 * 
	 * This method first check if a group with the given {@code group_id} already
	 * exist. If the group exist, it will logs a warning and return {@code false}.
	 * Otherwise, it proceed create a new group using the DAO layer and returns the
	 * result
	 * 
	 * @param group_id   the unique identifier of the group to be created
	 * @param group_name the name of the group
	 * @param createdBy  the ID of user creates the group
	 * @return {@code true} if the group was successfully created; {@code false} if
	 *         the group already exists
	 * @throws SQLException SQLException if a database access error occurs during
	 *                      group lookup or creation
	 * 
	 */
	public boolean createGroupService(String group_name, String user_id) throws SQLException {
		
		Group groupExist = groupDao.getGroupById(user_id);
		if (groupExist != null) {
			logger.warn("Creat failed: Group already exists");
			return false;
		}

		boolean success = groupDao.createGroupDao(group_name, user_id);
		logger.info("success: {}", success);
		return success;
	}

	/**
	 * 
	 * @param user_id
	 * @param group_id
	 */
	public void getUserInGroupService(UUID user_id, UUID group_id) {
  		try {
			groupDao.getUserInGroupByIdDao(user_id, group_id);
		} catch (Exception e) {
			
		}
	}
	
	public List<Group> getAllGroupService() {
	    try {
	        List<Group> groups = new ArrayList<>(groupDao.getAllGroupDao());

	        if (groups.isEmpty()) {
	            throw new RuntimeException("Can't get all group");
	        }

	        return groups;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        logger.error("Error when get all permission", e);

			throw new RuntimeException("System error when get all groups.");
	    }
	}
}
