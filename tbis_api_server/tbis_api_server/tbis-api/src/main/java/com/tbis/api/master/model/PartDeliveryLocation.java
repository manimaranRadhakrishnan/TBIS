package com.tbis.api.master.model;


public class PartDeliveryLocation {	
	
	private long partDeliveryDockId;
	private long partId;
	private int dLocationId;
	private int plantId;
	private int plantDockId; 
	private Boolean isActive;
	private int userId;

	public long getPartDeliveryDockId() {
		return partDeliveryDockId;
	}
	public void setPartDeliveryDockId(long partDeliveryDockId) {
		this.partDeliveryDockId = partDeliveryDockId;
	}
	public long getPartId() {
		return partId;
	}
	public void setPartId(long partId) {
		this.partId = partId;
	}
	public int getdLocationId() {
		return dLocationId;
	}
	public void setdLocationId(int dLocationId) {
		this.dLocationId = dLocationId;
	}
	public int getPlantId() {
		return plantId;
	}
	public void setPlantId(int plantId) {
		this.plantId = plantId;
	}
	public int getPlantDockId() {
		return plantDockId;
	}
	public void setPlantDockId(int plantDockId) {
		this.plantDockId = plantDockId;
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


