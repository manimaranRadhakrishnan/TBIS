package com.tbis.api.master.model;

public class Department {	
	private int cwdDeptId;
	private String cwdDeptName;
	private String cwdDeptShort;
	private Boolean cwdIsSystem; 
	private Boolean isActive; 
	private int userId;
	
	public int getCwdDeptId() {
		return cwdDeptId;
	}
	public void setCwdDeptId(int cwdDeptId) {
		this.cwdDeptId = cwdDeptId;
	}
	public String getCwdDeptName() {
		return cwdDeptName;
	}
	public void setCwdDeptName(String cwdDeptName) {
		this.cwdDeptName = cwdDeptName;
	}
	public String getCwdDeptShort() {
		return cwdDeptShort;
	}
	public void setCwdDeptShort(String cwdDeptShort) {
		this.cwdDeptShort = cwdDeptShort;
	}
	public Boolean getCwdIsSystem() {
		return cwdIsSystem;
	}
	public void setCwdIsSystem(Boolean cwdIsSystem) {
		this.cwdIsSystem = cwdIsSystem;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
		
}
