package com.tbis.api.master.model;

public class SOPMaster {	
	private long sopId;
	private int sopTypeid;
	private int sopSourceTypeid;
	private int warehouseId;
	private String sopName;
	private String sopDesc;
	private String sopNameLocal;
	private String sopDescLocal;
	private String sopIcon;
	private boolean isActive;
	private int userId;
	
	public long getSopId() {
		return sopId;
	}
	public void setSopId(long sopId) {
		this.sopId = sopId;
	}
	public int getSopTypeid() {
		return sopTypeid;
	}
	public void setSopTypeid(int sopTypeid) {
		this.sopTypeid = sopTypeid;
	}
	public int getSopSourceTypeid() {
		return sopSourceTypeid;
	}
	public void setSopSourceTypeid(int sopSourceTypeid) {
		this.sopSourceTypeid = sopSourceTypeid;
	}
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getSopName() {
		return sopName;
	}
	public void setSopName(String sopName) {
		this.sopName = sopName;
	}
	public String getSopDesc() {
		return sopDesc;
	}
	public void setSopDesc(String sopDesc) {
		this.sopDesc = sopDesc;
	}
	public String getSopNameLocal() {
		return sopNameLocal;
	}
	public void setSopNameLocal(String sopNameLocal) {
		this.sopNameLocal = sopNameLocal;
	}
	public String getSopDescLocal() {
		return sopDescLocal;
	}
	public void setSopDescLocal(String sopDescLocal) {
		this.sopDescLocal = sopDescLocal;
	}
	public String getSopIcon() {
		return sopIcon;
	}
	public void setSopIcon(String sopIcon) {
		this.sopIcon = sopIcon;
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
