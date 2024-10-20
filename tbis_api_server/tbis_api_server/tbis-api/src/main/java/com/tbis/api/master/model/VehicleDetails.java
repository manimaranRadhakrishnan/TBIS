package com.tbis.api.master.model;

public class VehicleDetails {
	private long pickListId;
	private String vehicleNo;
	private long vehicleTransId;
	private int userId;
	private long tripId;
	
	public long getPickListId() {
		return pickListId;
	}
	public void setPickListId(long pickListId) {
		this.pickListId = pickListId;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public long getVehicleTransId() {
		return vehicleTransId;
	}
	public void setVehicleTransId(long vehicleTransId) {
		this.vehicleTransId = vehicleTransId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getTripId() {
		return tripId;
	}
	public void setTripId(long tripId) {
		this.tripId = tripId;
	}
	
}
