package com.tbis.api.master.model;

public class InvoiceDetails {
	private long pickListDetailId;
	private String invoiceNo;
	private String ewayBillNo;
	private String consignementNo;
	private int userId;
	public long getPickListDetailId() {
		return pickListDetailId;
	}
	public void setPickListDetailId(long pickListDetailId) {
		this.pickListDetailId = pickListDetailId;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getEwayBillNo() {
		return ewayBillNo;
	}
	public void setEwayBillNo(String ewayBillNo) {
		this.ewayBillNo = ewayBillNo;
	}
	public String getConsignementNo() {
		return consignementNo;
	}
	public void setConsignementNo(String consignementNo) {
		this.consignementNo = consignementNo;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
	
	
	
}
