package com.tbis.api.master.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Transporter implements Serializable{
	private int vechicleId;
	private String vechicleNo;
	private String transporterCode;
	private String transporterName;
	private String vechicleSize;
	private int vechicleStatus;
	private String address;
	private String city;
	private String district;
	private String state;
	private String gstIn;
	private double lat;
	private double lang;
	private int locationId;
	private boolean isActive;
	private int userId;
	
	public int getVechicleId() {
		return vechicleId;
	}
	public void setVechicleId(int vechicleId) {
		this.vechicleId = vechicleId;
	}
	public String getVechicleNo() {
		return vechicleNo;
	}
	public void setVechicleNo(String vechicleNo) {
		this.vechicleNo = vechicleNo;
	}
	public String getTransporterCode() {
		return transporterCode;
	}
	public void setTransporterCode(String transporterCode) {
		this.transporterCode = transporterCode;
	}
	public String getTransporterName() {
		return transporterName;
	}
	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}
	public String getVechicleSize() {
		return vechicleSize;
	}
	public void setVechicleSize(String vechicleSize) {
		this.vechicleSize = vechicleSize;
	}
	public int getVechicleStatus() {
		return vechicleStatus;
	}
	public void setVechicleStatus(int vechicleStatus) {
		this.vechicleStatus = vechicleStatus;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getGstIn() {
		return gstIn;
	}
	public void setGstIn(String gstIn) {
		this.gstIn = gstIn;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLang() {
		return lang;
	}
	public void setLang(double lang) {
		this.lang = lang;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	
	
}
