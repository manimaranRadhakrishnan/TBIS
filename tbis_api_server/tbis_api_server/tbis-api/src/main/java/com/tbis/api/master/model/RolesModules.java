package com.tbis.api.master.model;

public class RolesModules {
	private int roleModuleId;
	private String roleId;
	private String roleMenuId;
	private int userId;
	
	public int getRoleModuleId() {
		return roleModuleId;
	}
	public void setRoleModuleId(int roleModuleId) {
		this.roleModuleId = roleModuleId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleMenuId() {
		return roleMenuId;
	}
	public void setRoleMenuId(String roleMenuId) {
		this.roleMenuId = roleMenuId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
