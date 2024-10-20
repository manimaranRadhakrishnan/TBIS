package com.tbis.api.master.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Role implements Serializable{
	private int roleId;
	private String roleName;
	private String roleDescription;
	private int parentRoleId;
	private String parentRoleName;
	private ArrayList<RoleMenu> roleMenus;
	private ArrayList<RoleOperation> roleOperations;
	private ArrayList<RoleOperation> roleColumns;
	private int userId;
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public int getParentRoleId() {
		return parentRoleId;
	}
	public void setParentRoleId(int parentRoleId) {
		this.parentRoleId = parentRoleId;
	}
	public String getParentRoleName() {
		return parentRoleName;
	}
	public void setParentRoleName(String parentRoleName) {
		this.parentRoleName = parentRoleName;
	}
	public ArrayList<RoleMenu> getRoleMenus() {
		return roleMenus;
	}
	public void setRoleMenus(ArrayList<RoleMenu> roleMenus) {
		this.roleMenus = roleMenus;
	}
	public ArrayList<RoleOperation> getRoleOperations() {
		return roleOperations;
	}
	public void setRoleOperations(ArrayList<RoleOperation> roleOperations) {
		this.roleOperations = roleOperations;
	}
	public ArrayList<RoleOperation> getRoleColumns() {
		return roleColumns;
	}
	public void setRoleColumns(ArrayList<RoleOperation> roleColumns) {
		this.roleColumns = roleColumns;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	
}
