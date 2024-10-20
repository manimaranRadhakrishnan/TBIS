package com.tbis.api.master.model;

public class CustomerLineMap {
	private long customerLineSpaceId;
	private long customerId;
	private long lineSpaceId;
	private int startCol;
	private int endCol; 
	private boolean isActive;
	private int userId;
	
	public long getCustomerLineSpaceId() {
		return customerLineSpaceId;
	}
	public void setCustomerLineSpaceId(long customerLineSpaceId) {
		this.customerLineSpaceId = customerLineSpaceId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public long getLineSpaceId() {
		return lineSpaceId;
	}
	public void setLineSpaceId(long lineSpaceId) {
		this.lineSpaceId = lineSpaceId;
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
	public int getStartCol() {
		return startCol;
	}
	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}
	public int getEndCol() {
		return endCol;
	}
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}

	
}
