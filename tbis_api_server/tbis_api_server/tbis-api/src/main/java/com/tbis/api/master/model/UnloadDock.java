package com.tbis.api.master.model;

public class UnloadDock {
	private int udcId;
	private String udcName;
	private int subLocationId;
	private boolean isActive;
	private int userId;
	private int warehouseId;
	private int locationId;
	
	public int getUdcId() {
		return udcId;
	}
	public void setUdcId(int udcId) {
		this.udcId = udcId;
	}
	public String getUdcName() {
		return udcName;
	}
	public void setUdcName(String udcName) {
		this.udcName = udcName;
	}
	public int getSubLocationId() {
		return subLocationId;
	}
	public void setSubLocationId(int subLocationId) {
		this.subLocationId = subLocationId;
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
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	
}
