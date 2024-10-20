package com.tbis.api.master.model;

public class CustomerPartsLineMap {
	private long customerPartId;
	private long customerId;
	private long partId;
	private String customerPartCode;
	private int cardConfigId;
	private long lineSpaceId;
	private long lineLotId;
	private long lineRackId;
	private int maxQty;
	private int isActive;
	private int userId;

	public long getCustomerPartId() {
		return customerPartId;
	}

	public void setCustomerPartId(long customerPartId) {
		this.customerPartId = customerPartId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public long getPartId() {
		return partId;
	}

	public void setPartId(long partId) {
		this.partId = partId;
	}

	public String getCustomerPartCode() {
		return customerPartCode;
	}

	public void setCustomerPartCode(String customerPartCode) {
		this.customerPartCode = customerPartCode;
	}

	

	public long getLineSpaceId() {
		return lineSpaceId;
	}

	public void setLineSpaceId(long lineSpaceId) {
		this.lineSpaceId = lineSpaceId;
	}

	public long getLineLotId() {
		return lineLotId;
	}

	public void setLineLotId(long lineLotId) {
		this.lineLotId = lineLotId;
	}

	public long getLineRackId() {
		return lineRackId;
	}

	public void setLineRackId(long lineRackId) {
		this.lineRackId = lineRackId;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCardConfigId() {
		return cardConfigId;
	}

	public void setCardConfigId(int cardConfigId) {
		this.cardConfigId = cardConfigId;
	}

	public int getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(int maxQty) {
		this.maxQty = maxQty;
	}

}
