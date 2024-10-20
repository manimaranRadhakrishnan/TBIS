package com.tbis.api.master.model;

public class Designation {	
	private int cwdDesigId;
	private String cwdDesigName;
	private String cwdDesigShort;
	private Boolean cwdIsSystem;
	private Boolean isActive;
	private int userId;
	
	public int getCwdDesigId() {
		return cwdDesigId;
	}
	public void setCwdDesigId(int cwdDesigId) {
		this.cwdDesigId = cwdDesigId;
	}
	public String getCwdDesigName() {
		return cwdDesigName;
	}
	public void setCwdDesigName(String cwdDesigName) {
		this.cwdDesigName = cwdDesigName;
	}
	public String getCwdDesigShort() {
		return cwdDesigShort;
	}
	public void setCwdDesigShort(String cwdDesigShort) {
		this.cwdDesigShort = cwdDesigShort;
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
