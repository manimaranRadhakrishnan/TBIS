package com.tbis.api.master.model;

public class SubLocation {
	private int subLocationId;
	private int warehouseId;
	private String subLocationName;
	private String subLocationShortCode;
	private double areaInSqFeet;
	private double areaInM2;
	private double areaInM3;
	private double customerStorageArea;
	private double 	slLength;
	private double 	slWidth;
	private double  slHeight;
	private double 	uLength;
	private double 	uWidth;
	private int  	slRows;
	private int		slColumns;
	
	private boolean isActive;
	private int userId;
	
	public int getSubLocationId() {
		return subLocationId;
	}
	public void setSubLocationId(int subLocationId) {
		this.subLocationId = subLocationId;
	}
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getSubLocationName() {
		return subLocationName;
	}
	public void setSubLocationName(String subLocationName) {
		this.subLocationName = subLocationName;
	}
	public String getSubLocationShortCode() {
		return subLocationShortCode;
	}
	public void setSubLocationShortCode(String subLocationShortCode) {
		this.subLocationShortCode = subLocationShortCode;
	}
	public double getAreaInSqFeet() {
		return areaInSqFeet;
	}
	public void setAreaInSqFeet(double areaInSqFeet) {
		this.areaInSqFeet = areaInSqFeet;
	}
	public double getAreaInM2() {
		return areaInM2;
	}
	public void setAreaInM2(double areaInM2) {
		this.areaInM2 = areaInM2;
	}
	public double getCustomerStorageArea() {
		return customerStorageArea;
	}
	public void setCustomerStorageArea(double customerStorageArea) {
		this.customerStorageArea = customerStorageArea;
	}
	
	public double getSlLength() {
		return slLength;
	}
	public void setSlLength(double slLength) {
		this.slLength = slLength;
	}
	public double getSlWidth() {
		return slWidth;
	}
	public void setSlWidth(double slWidth) {
		this.slWidth = slWidth;
	}
	public double getSlHeight() {
		return slHeight;
	}
	public void setSlHeight(double slHeight) {
		this.slHeight = slHeight;
	}
	
	public double getAreaInM3() {
		return areaInM3;
	}
	public void setAreaInM3(double areaInM3) {
		this.areaInM3 = areaInM3;
	}
	public boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public double getuLength() {
		return uLength;
	}
	public void setuLength(double uLength) {
		this.uLength = uLength;
	}
	public double getuWidth() {
		return uWidth;
	}
	public void setuWidth(double uWidth) {
		this.uWidth = uWidth;
	}
	public int getSlRows() {
		return slRows;
	}
	public void setSlRows(int slRows) {
		this.slRows = slRows;
	}
	public int getSlColumns() {
		return slColumns;
	}
	public void setSlColumns(int slColumns) {
		this.slColumns = slColumns;
	}
	
}
