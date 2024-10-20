package com.tbis.api.master.model;

import java.io.Serializable;

public class VendorFillRate implements Serializable{	
	private int customerId;
	private String customerCode;
	private String customerName;
	private String partName;
	private int partId;
	private String partNo;
	private int totalAsn;
	private int totalConfirmed;
	private int totalAccepted;
	private int totalDispatch;
	private int totalDispatchConfirmed;
	private int totalReceipts;
	private int totalPending;
	private double fillRate;
	
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getTotalAsn() {
		return totalAsn;
	}
	public void setTotalAsn(int totalAsn) {
		this.totalAsn = totalAsn;
	}
	public int getTotalAccepted() {
		return totalAccepted;
	}
	public void setTotalAccepted(int totalAccepted) {
		this.totalAccepted = totalAccepted;
	}
	public int getTotalDispatch() {
		return totalDispatch;
	}
	public void setTotalDispatch(int totalDispatch) {
		this.totalDispatch = totalDispatch;
	}
	public int getTotalReceipts() {
		return totalReceipts;
	}
	public void setTotalReceipts(int totalReceipts) {
		this.totalReceipts = totalReceipts;
	}
	public int getTotalPending() {
		return totalPending;
	}
	public void setTotalPending(int totalPending) {
		this.totalPending = totalPending;
	}
	public double getFillRate() {
		return fillRate;
	}
	public void setFillRate(double fillRate) {
		this.fillRate = fillRate;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	public int getPartId() {
		return partId;
	}
	public void setPartId(int partId) {
		this.partId = partId;
	}
	public String getPartNo() {
		return partNo;
	}
	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	public int getTotalConfirmed() {
		return totalConfirmed;
	}
	public void setTotalConfirmed(int totalConfirmed) {
		this.totalConfirmed = totalConfirmed;
	}
	public int getTotalDispatchConfirmed() {
		return totalDispatchConfirmed;
	}
	public void setTotalDispatchConfirmed(int totalDispatchConfirmed) {
		this.totalDispatchConfirmed = totalDispatchConfirmed;
	}
	
		
}


