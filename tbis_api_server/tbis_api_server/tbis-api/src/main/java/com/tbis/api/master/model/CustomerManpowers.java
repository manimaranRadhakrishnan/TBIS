package com.tbis.api.master.model;

public class CustomerManpowers {
	private long manpowerId;
	private long customerId;
	private String manpowerDetail;
	private int shiftA;
	private int shiftB;
	private int categoryId;
	private boolean isActive;
	private int userId;
	
	public long getManpowerId() {
		return manpowerId;
	}
	public void setManpowerId(long manpowerId) {
		this.manpowerId = manpowerId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getManpowerDetail() {
		return manpowerDetail;
	}
	public void setManpowerDetail(String manpowerDetail) {
		this.manpowerDetail = manpowerDetail;
	}
	public int getShiftA() {
		return shiftA;
	}
	public void setShiftA(int shiftA) {
		this.shiftA = shiftA;
	}
	public int getShiftB() {
		return shiftB;
	}
	public void setShiftB(int shiftB) {
		this.shiftB = shiftB;
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
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
		
}
