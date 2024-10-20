package com.tbis.api.master.model;

public class PartKitMaster {
	private long partKitMasterId;
	private int partIdEndUser;
	private int partIdCustomer;
	private boolean isActive;
	private int userId;
	
	public long getPartKitMasterId() {
		return partKitMasterId;
	}
	public void setPartKitMasterId(long partKitMasterId) {
		this.partKitMasterId = partKitMasterId;
	}
	public int getPartIdEndUser() {
		return partIdEndUser;
	}
	public void setPartIdEndUser(int partIdEndUser) {
		this.partIdEndUser = partIdEndUser;
	}
	public int getPartIdCustomer() {
		return partIdCustomer;
	}
	public void setPartIdCustomer(int partIdCustomer) {
		this.partIdCustomer = partIdCustomer;
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
