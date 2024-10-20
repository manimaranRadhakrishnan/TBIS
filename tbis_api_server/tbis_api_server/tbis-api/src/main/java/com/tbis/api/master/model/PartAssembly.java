package com.tbis.api.master.model;


public class PartAssembly {	
	
	private long partassemblyId;
	private String assemblyPartname; 
	private String assemblyPartcode;
	private int qty;
	private long primaryPartid;
	private Boolean isActive;
	private int userId;
	public long getPartassemblyId() {
		return partassemblyId;
	}
	public void setPartassemblyId(long partassemblyId) {
		this.partassemblyId = partassemblyId;
	}
	public String getAssemblyPartname() {
		return assemblyPartname;
	}
	public void setAssemblyPartname(String assemblyPartname) {
		this.assemblyPartname = assemblyPartname;
	}
	public String getAssemblyPartcode() {
		return assemblyPartcode;
	}
	public void setAssemblyPartcode(String assemblyPartcode) {
		this.assemblyPartcode = assemblyPartcode;
	}
	public long getPrimaryPartid() {
		return primaryPartid;
	}
	public void setPrimaryPartid(long primaryPartid) {
		this.primaryPartid = primaryPartid;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	
}


