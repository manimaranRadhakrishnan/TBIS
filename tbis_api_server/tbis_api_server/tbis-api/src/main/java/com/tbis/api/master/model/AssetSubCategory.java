package com.tbis.api.master.model;

public class AssetSubCategory {	
	private int cwascId;
	private int cwacId;
	private String cwascName;
	private String cwascShort;
	private Boolean cwascIsSystem;
	private Boolean isActive; 
	private int userId;
	
	public int getCwascId() {
		return cwascId;
	}
	public void setCwascId(int cwascId) {
		this.cwascId = cwascId;
	}
	public int getCwacId() {
		return cwacId;
	}
	public void setCwacId(int cwacId) {
		this.cwacId = cwacId;
	}
	public String getCwascName() {
		return cwascName;
	}
	public void setCwascName(String cwascName) {
		this.cwascName = cwascName;
	}
	public String getCwascShort() {
		return cwascShort;
	}
	public void setCwascShort(String cwascShort) {
		this.cwascShort = cwascShort;
	}
	public Boolean getCwascIsSystem() {
		return cwascIsSystem;
	}
	public void setCwascIsSystem(Boolean cwascIsSystem) {
		this.cwascIsSystem = cwascIsSystem;
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
