package com.tbis.api.master.model;

public class AssetCategoryBudget {	
	private int cwacbId;
	private int cwacId;
	private String cwacbPeriod;
	private double cwacbBudget;
	private double cwacbBudgetUsed;
	private int cwacbWarehouseId;
	private Boolean isActive; 
	private int userId;
	
	public int getCwacbId() {
		return cwacbId;
	}
	public void setCwacbId(int cwacbId) {
		this.cwacbId = cwacbId;
	}
	public String getCwacbPeriod() {
		return cwacbPeriod;
	}
	public void setCwacbPeriod(String cwacbPeriod) {
		this.cwacbPeriod = cwacbPeriod;
	}
	public double getCwacbBudget() {
		return cwacbBudget;
	}
	public void setCwacbBudget(double cwacbBudget) {
		this.cwacbBudget = cwacbBudget;
	}
	public double getCwacbBudgetUsed() {
		return cwacbBudgetUsed;
	}
	public void setCwacbBudgetUsed(double cwacbBudgetUsed) {
		this.cwacbBudgetUsed = cwacbBudgetUsed;
	}
	public int getCwacbWarehouseId() {
		return cwacbWarehouseId;
	}
	public void setCwacbWarehouseId(int cwacbWarehouseId) {
		this.cwacbWarehouseId = cwacbWarehouseId;
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
	public int getCwacId() {
		return cwacId;
	}
	public void setCwacId(int cwacId) {
		this.cwacId = cwacId;
	}
	
	
}
