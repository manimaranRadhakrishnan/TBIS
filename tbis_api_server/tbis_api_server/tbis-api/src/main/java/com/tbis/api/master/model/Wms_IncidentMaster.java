package com.tbis.api.master.model;

public class Wms_IncidentMaster {
	private int incidentId;
	private String incidentName;
	private int incidentCatergoryId;
	private boolean isSystem;
	private boolean isActive;
	private int userId;
	
	public int getIncidentId() {
		return incidentId;
	}
	public void setIncidentId(int incidentId) {
		this.incidentId = incidentId;
	}
	public String getIncidentName() {
		return incidentName;
	}
	public void setIncidentName(String incidentName) {
		this.incidentName = incidentName;
	}
	public int getIncidentCatergoryId() {
		return incidentCatergoryId;
	}
	public void setIncidentCatergoryId(int incidentCatergoryId) {
		this.incidentCatergoryId = incidentCatergoryId;
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
