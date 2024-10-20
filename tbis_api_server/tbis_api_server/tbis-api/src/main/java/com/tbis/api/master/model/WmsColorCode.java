package com.tbis.api.master.model;

public class WmsColorCode {
	private int colorCodeId;
	private String colorName;
	private boolean isSystem;
	private boolean isActive;
	private int userId;
	
	public int getColorCodeId() {
		return colorCodeId;
	}
	public void setColorCodeId(int colorCodeId) {
		this.colorCodeId = colorCodeId;
	}
	public String getColorName() {
		return colorName;
	}
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
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
