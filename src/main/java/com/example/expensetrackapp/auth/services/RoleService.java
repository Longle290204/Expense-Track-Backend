package com.example.expensetrackapp.auth.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.expensetrackapp.auth.dao.RoleDAO;
import com.example.expensetrackapp.auth.models.Group;
import com.example.expensetrackapp.auth.models.Permission;
import com.example.expensetrackapp.auth.models.Role;
import com.example.expensetrackapp.auth.models.User;
import com.example.expensetrackapp.custom.BusinessException;
import com.example.expensetrackapp.group.dao.GroupDAO;

public class RoleService {
	private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

	private RoleDAO roleDao;
	private GroupDAO groupDao;

	public RoleService() {
		this.roleDao = new RoleDAO();
		this.groupDao = new GroupDAO();
	}

	/**
	 * Thêm một vai trò (role) mới vào một nhóm cụ thể.
	 *
	 * Phương thức này gọi đến tầng DAO để thực hiện việc thêm role vào cơ sở dữ
	 * liệu và trả về true nếu thêm thành công, ngược lại trả về false.
	 * 
	 * @param role_name Tên của vai trò cần thêm.
	 * @param group_id  ID của nhóm mà vai trò sẽ được gán vào.
	 * @param is_system Đánh dấu vai trò này có phải là vai trò hệ thống hay không
	 *                  (tùy thuộc vào logic sử dụng, có thể dùng sau này).
	 * @return {@code true} nếu thêm vai trò thành công, {@code false} nếu thất bại.
	 * @throws SQLException nếu có lỗi xảy ra trong quá trình tương tác với cơ sở dữ
	 *                      liệu.
	 */
	public boolean addRoleInGroupService(String role_name, UUID group_id, Boolean is_system) throws SQLException {

		Role role = roleDao.addRoleInGroup(role_name, group_id, is_system);

		if (role != null) {
			logger.info("Role '{}' created successfully", role_name);
			return true;
		} else {
			logger.error("Failed to creating role");
			return false;
		}

	}

	/**
	 * 
	 * Delete role in group
	 * 
	 * @param group_id
	 * @param role_id
	 * 
	 */
	public void deleteRoleInGroupService(UUID group_id, UUID role_id) {
		try {
			boolean deleted = roleDao.deleteRoleInGroup(role_id, group_id);

			if (!deleted) {
				throw new RuntimeException("Not found role in corresponding group");
			}

		} catch (SQLException e) {
			logger.error("Lỗi khi xóa role trong group", e);

			throw new RuntimeException("System error when deleting role.");

		}
	}

	/**
	 * Update role in group
	 * 
	 * @param role_id
	 * @param role_name
	 * @param group_id
	 */
	public void updateRoleInGroupService(UUID role_id, String role_name, UUID group_id) {
		try {
			boolean updated = roleDao.updateRoleInGroup(role_id, role_name, group_id);

			if (!updated) {
				throw new RuntimeException("Not found role in corresponding group");
			}
		} catch (SQLException e) {
			logger.error("Error when update role in group", e);

			throw new RuntimeException("System error when update role.");
		}
	}

	/**
	 * Thêm permission vào role
	 * 
	 * @param role_id
	 * @param group_id
	 * @param psermissionIds
	 */
	public void addPermissionToRoleInGroupService(UUID role_id, UUID group_id, List<UUID> psermissionIds) {
		try {
			boolean isAddPermissionToRole = roleDao.addPermissionToRoleInGroup(role_id, group_id, psermissionIds);

			if (!isAddPermissionToRole) {
				throw new RuntimeException("Can't add permission to role");
			}
		} catch (SQLException e) {
			logger.error("Error when add permission to role in group", e);

			throw new RuntimeException("System error when update role.");
		}
	}

	public Set<Permission> getPermissionFromRoleInGroupService(UUID role_id, UUID group_id) {
		try {
			Set<Permission> permissions = roleDao.getPermissionFromRoleInGroup(role_id, group_id);

			if (permissions.isEmpty()) {
				throw new RuntimeException("Can't get permissions from role");
			}

			return permissions;
		} catch (SQLException e) {
			logger.error("Error when get permission from role in group", e);

			throw new RuntimeException("System error when get permission from role.");
		}

	}

	public void assignRoleToUserService(UUID user_id, UUID group_id, UUID role_id) {

		try {
			User isUserInGroup = groupDao.getUserInGroupByIdDao(user_id, group_id);

			// Check trường hợp nếu user không có trong nhóm
			if (isUserInGroup == null) {
				throw new BusinessException("User must belong to a group before assigning a role");
			}

			boolean assignSuccess = roleDao.assignRolesToUserDao(user_id, group_id, role_id);
			if (!assignSuccess) {
				throw new RuntimeException("Can't assign role to user from dao");
			}

		} catch (SQLException e) {
			logger.error("Error when  assign role to user in group", e);

			throw new RuntimeException("System error when  assign role to user.");
		}
	}
}
