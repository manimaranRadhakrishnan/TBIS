package com.tbis.api.master.model;

import java.io.Serializable;

public class Location implements Serializable{
	private String locationName;
	private int locationId;
	private String locationShortCode;
	private int isActive;
	private int userId;
	
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public String getLocationShortCode() {
		return locationShortCode;
	}
	public void setLocationShortCode(String locationShortCode) {
		this.locationShortCode = locationShortCode;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
