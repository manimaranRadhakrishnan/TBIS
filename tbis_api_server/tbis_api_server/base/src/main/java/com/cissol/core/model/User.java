package com.cissol.core.model;

import java.security.Principal;

public class User implements Principal {
	private String userName;
	private int userId;
	private String mobileNo;
	private String emailId;
	private int accessMobileSite;
	private int roleId;
	private String token;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public int getAccessMobileSite() {
		return accessMobileSite;
	}
	public void setAccessMobileSite(int accessMobileSite) {
		this.accessMobileSite = accessMobileSite;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	@Override
    public String getName() {
        return this.userName;
    }
}
