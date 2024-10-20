package com.tbis.api.master.model;

public class WmsIncidentCategoryMaster {
	private int incidentCategoryId;
	private String incidentCategoryName;
	private boolean isSystem;
	private boolean isActive;
	private int userId;
	
	public int getIncidentCategoryId() {
		return incidentCategoryId;
	}
	public void setIncidentCategoryId(int incidentCategoryId) {
		this.incidentCategoryId = incidentCategoryId;
	}
	public String getIncidentCategoryName() {
		return incidentCategoryName;
	}
	public void setIncidentCategoryName(String incidentCategoryName) {
		this.incidentCategoryName = incidentCategoryName;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
