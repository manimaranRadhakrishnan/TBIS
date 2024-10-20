package com.tbis.api.master.model;

public class RoleColumnMap {
	private int rcmRoleId;
	private int rcmReportId;
	private String rcmColumnName;
	private int rcmMenuId;
	private int dummy;
	private int userId;
	
	public int getRcmRoleId() {
		return rcmRoleId;
	}
	public void setRcmRoleId(int rcmRoleId) {
		this.rcmRoleId = rcmRoleId;
	}
	public int getRcmReportId() {
		return rcmReportId;
	}
	public void setRcmReportId(int rcmReportId) {
		this.rcmReportId = rcmReportId;
	}
	public String getRcmColumnName() {
		return rcmColumnName;
	}
	public void setRcmColumnName(String rcmColumnName) {
		this.rcmColumnName = rcmColumnName;
	}
	public int getRcmMenuId() {
		return rcmMenuId;
	}
	public void setRcmMenuId(int rcmMenuId) {
		this.rcmMenuId = rcmMenuId;
	}
	public int getDummy() {
		return dummy;
	}
	public void setDummy(int dummy) {
		this.dummy = dummy;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
