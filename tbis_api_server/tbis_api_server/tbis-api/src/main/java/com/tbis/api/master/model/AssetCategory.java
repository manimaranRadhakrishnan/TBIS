package com.tbis.api.master.model;

public class AssetCategory {	
	private int cwacId;
	private String cwacName;
	private String cwacShort;
	private Boolean cwacIsSystem;
	private Boolean isActive; 
	private int userId;
	
	public int getCwacId() {
		return cwacId;
	}
	public void setCwacId(int cwacId) {
		this.cwacId = cwacId;
	}
	public String getCwacName() {
		return cwacName;
	}
	public void setCwacName(String cwacName) {
		this.cwacName = cwacName;
	}
	public String getCwacShort() {
		return cwacShort;
	}
	public void setCwacShort(String cwacShort) {
		this.cwacShort = cwacShort;
	}
	public Boolean getCwacIsSystem() {
		return cwacIsSystem;
	}
	public void setCwacIsSystem(Boolean cwacIsSystem) {
		this.cwacIsSystem = cwacIsSystem;
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
