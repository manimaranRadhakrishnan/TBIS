package com.tbis.api.master.model;


public class PlantDocks {	
	
	private int dDockId;
	private String dockName; 
	private String dockShortName;
	private int dLocationId;
	private Boolean isActive;
	private int userId;
	
	public int getdDockId() {
		return dDockId;
	}
	public void setdDockId(int dDockId) {
		this.dDockId = dDockId;
	}
	public String getDockName() {
		return dockName;
	}
	public void setDockName(String dockName) {
		this.dockName = dockName;
	}
	public String getDockShortName() {
		return dockShortName;
	}
	public void setDockShortName(String dockShortName) {
		this.dockShortName = dockShortName;
	}
	public int getdLocationId() {
		return dLocationId;
	}
	public void setdLocationId(int dLocationId) {
		this.dLocationId = dLocationId;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
}


