package com.tbis.api.master.model;

public class TransportVehicle {
	private long vehicleId;
	private long transportId;
	private String vehicleNo;
	private String vechicleSize;
	private double vehicleCapacity;
	private int vehicleAxix;
	private double vehicleHeight;
	private double vehicleWidth;
	private double vehicleLength;
	private double m2;
	private double m3;
	private boolean isActive;
	private int userId;
	
	public long getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}
	public long getTransportId() {
		return transportId;
	}
	public void setTransportId(long transportId) {
		this.transportId = transportId;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public String getVechicleSize() {
		return vechicleSize;
	}
	public void setVechicleSize(String vechicleSize) {
		this.vechicleSize = vechicleSize;
	}
	public double getVehicleCapacity() {
		return vehicleCapacity;
	}
	public void setVehicleCapacity(double vehicleCapacity) {
		this.vehicleCapacity = vehicleCapacity;
	}
	public int getVehicleAxix() {
		return vehicleAxix;
	}
	public void setVehicleAxix(int vehicleAxix) {
		this.vehicleAxix = vehicleAxix;
	}
	public double getVehicleHeight() {
		return vehicleHeight;
	}
	public void setVehicleHeight(double vehicleHeight) {
		this.vehicleHeight = vehicleHeight;
	}
	public double getVehicleWidth() {
		return vehicleWidth;
	}
	public void setVehicleWidth(double vehicleWidth) {
		this.vehicleWidth = vehicleWidth;
	}
	public double getVehicleLength() {
		return vehicleLength;
	}
	public void setVehicleLength(double vehicleLength) {
		this.vehicleLength = vehicleLength;
	}
	public double getM2() {
		return m2;
	}
	public void setM2(double m2) {
		this.m2 = m2;
	}
	public double getM3() {
		return m3;
	}
	public void setM3(double m3) {
		this.m3 = m3;
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
