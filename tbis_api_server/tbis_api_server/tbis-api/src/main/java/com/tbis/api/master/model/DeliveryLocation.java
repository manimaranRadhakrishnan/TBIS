package com.tbis.api.master.model;


public class DeliveryLocation {	
	private String dLocationName;
	private int dLocationId;
	private String dLocationShortCode;
	private int locationId;
	private Boolean isActive;
	private int userId;
	
	public String getdLocationName() {
		return dLocationName;
	}
	public void setdLocationName(String dLocationName) {
		this.dLocationName = dLocationName;
	}
	public int getdLocationId() {
		return dLocationId;
	}
	public void setdLocationId(int dLocationId) {
		this.dLocationId = dLocationId;
	}
	public String getdLocationShortCode() {
		return dLocationShortCode;
	}
	public void setdLocationShortCode(String dLocationShortCode) {
		this.dLocationShortCode = dLocationShortCode;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
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


