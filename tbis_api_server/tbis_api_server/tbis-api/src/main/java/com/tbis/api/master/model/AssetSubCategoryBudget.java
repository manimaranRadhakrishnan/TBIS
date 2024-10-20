package com.tbis.api.master.model;

public class AssetSubCategoryBudget {	
	private int cwascbId;
	private int cwascId;
	private int cwascbWarehouseId;
	private double cwascbBudget;
	private double cwascbBudgetUsed;
	private Boolean cwascbIsSystem;
	private Boolean isActive; 
	private int userId;
	
	public int getCwascbId() {
		return cwascbId;
	}
	public void setCwascbId(int cwascbId) {
		this.cwascbId = cwascbId;
	}
	public int getCwascId() {
		return cwascId;
	}
	public void setCwascId(int cwascId) {
		this.cwascId = cwascId;
	}
	public int getCwascbWarehouseId() {
		return cwascbWarehouseId;
	}
	public void setCwascbWarehouseId(int cwascbWarehouseId) {
		this.cwascbWarehouseId = cwascbWarehouseId;
	}
	public double getCwascbBudget() {
		return cwascbBudget;
	}
	public void setCwascbBudget(double cwascbBudget) {
		this.cwascbBudget = cwascbBudget;
	}
	public double getCwascbBudgetUsed() {
		return cwascbBudgetUsed;
	}
	public void setCwascbBudgetUsed(double cwascbBudgetUsed) {
		this.cwascbBudgetUsed = cwascbBudgetUsed;
	}
	public Boolean getCwascbIsSystem() {
		return cwascbIsSystem;
	}
	public void setCwascbIsSystem(Boolean cwascbIsSystem) {
		this.cwascbIsSystem = cwascbIsSystem;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
		
}
