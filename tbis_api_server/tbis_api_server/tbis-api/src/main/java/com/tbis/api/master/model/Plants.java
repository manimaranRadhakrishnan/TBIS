package com.tbis.api.master.model;


public class Plants {	
	
	private int plantId;
	private String plantName; 
	private String plantShorName;
	private int dLocationId;
	private Boolean isActive;
	private int userId;
	public int getPlantId() {
		return plantId;
	}
	public void setPlantId(int plantId) {
		this.plantId = plantId;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public String getPlantShorName() {
		return plantShorName;
	}
	public void setPlantShorName(String plantShorName) {
		this.plantShorName = plantShorName;
	}
	public int getdLocationId() {
		return dLocationId;
	}
	public void setdLocationId(int dLocationId) {
		this.dLocationId = dLocationId;
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


