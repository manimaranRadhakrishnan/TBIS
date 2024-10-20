package com.tbis.api.master.model;

import java.util.ArrayList;

public class VechicleIntrance {	
	private long transId;
	private String vechicleNo;
	private String driverName;
	private String driverMobile;
	private int statusId;;
	private int userId;
	private String returnPack;
	private int returnPackQty;
	private ArrayList<ScanDetails> scanDetails;
	
	public long getTransId() {
		return transId;
	}
	public void setTransId(long transId) {
		this.transId = transId;
	}
	public String getVechicleNo() {
		return vechicleNo;
	}
	public void setVechicleNo(String vechicleNo) {
		this.vechicleNo = vechicleNo;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverMobile() {
		return driverMobile;
	}
	public void setDriverMobile(String driverMobile) {
		this.driverMobile = driverMobile;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getReturnPack() {
		return returnPack;
	}
	public void setReturnPack(String returnPack) {
		this.returnPack = returnPack;
	}
	public int getReturnPackQty() {
		return returnPackQty;
	}
	public void setReturnPackQty(int returnPackQty) {
		this.returnPackQty = returnPackQty;
	}
	public ArrayList<ScanDetails> getScanDetails() {
		return scanDetails;
	}
	public void setScanDetails(ArrayList<ScanDetails> scanDetails) {
		this.scanDetails = scanDetails;
	}
	
	
		
}
