package com.tbis.api.master.model;

public class CustomerSoftwares {
	private long softwareId;
	private long customerId;
	private String softwareName;
	private String softwareUrl;
	private String softwareUserName;
	private String softwarePassword;
	private boolean isActive;
	private int userId;
	
	public long getSoftwareId() {
		return softwareId;
	}
	public void setSoftwareId(long softwareId) {
		this.softwareId = softwareId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getSoftwareName() {
		return softwareName;
	}
	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}
	public String getSoftwareUrl() {
		return softwareUrl;
	}
	public void setSoftwareUrl(String softwareUrl) {
		this.softwareUrl = softwareUrl;
	}
	public String getSoftwareUserName() {
		return softwareUserName;
	}
	public void setSoftwareUserName(String softwareUserName) {
		this.softwareUserName = softwareUserName;
	}
	public String getSoftwarePassword() {
		return softwarePassword;
	}
	public void setSoftwarePassword(String softwarePassword) {
		this.softwarePassword = softwarePassword;
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
	
		
}
