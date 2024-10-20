package com.tbis.api.master.model;

public class CustomerLineRackMap {
	private long customerLineRackId;
	private long customerId;
	private long lineRackId;
	private boolean isActive;
	private int userId;
	
	public long getCustomerLineRackId() {
		return customerLineRackId;
	}
	public void setCustomerLineRackId(long customerLineRackId) {
		this.customerLineRackId = customerLineRackId;
	}
	public long getLineRackId() {
		return lineRackId;
	}
	public void setLineRackId(long lineRackId) {
		this.lineRackId = lineRackId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
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

	
}
