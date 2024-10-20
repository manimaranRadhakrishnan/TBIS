package com.tbis.api.master.model;

public class EntityContracts {
	private long contractId;
	private long entityId;
	private long entityTypeId;
	private String contractNo;
	private String poNo;
	private String startDate;
	private String endDate;
	private boolean isActive;
	private int userId;
	private String customerSignOff;
	private int tbisSignOff;
	private int warehouseSpace;
	private int billingCycle;
	private double creditLimit;
	private int contractType;
	
	public long getContractId() {
		return contractId;
	}
	public void setContractId(long contractId) {
		this.contractId = contractId;
	}
	
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	public long getEntityTypeId() {
		return entityTypeId;
	}
	public void setEntityTypeId(long entityTypeId) {
		this.entityTypeId = entityTypeId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getPoNo() {
		return poNo;
	}
	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
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
	public String getCustomerSignOff() {
		return customerSignOff;
	}
	public void setCustomerSignOff(String customerSignOff) {
		this.customerSignOff = customerSignOff;
	}
	public int getTbisSignOff() {
		return tbisSignOff;
	}
	public void setTbisSignOff(int tbisSignOff) {
		this.tbisSignOff = tbisSignOff;
	}
	public int getWarehouseSpace() {
		return warehouseSpace;
	}
	public void setWarehouseSpace(int warehouseSpace) {
		this.warehouseSpace = warehouseSpace;
	}
	public int getBillingCycle() {
		return billingCycle;
	}
	public void setBillingCycle(int billingCycle) {
		this.billingCycle = billingCycle;
	}
	public double getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}
	public int getContractType() {
		return contractType;
	}
	public void setContractType(int contractType) {
		this.contractType = contractType;
	}
	
	
	
}
