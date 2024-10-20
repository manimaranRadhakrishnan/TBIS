package com.tbis.api.master.model;

public class CustomerContacts 
{
	private long contactId;
	private long customerId;
	private String contactPersonName;
	private String contactPersonDesignation;
	private String contactEmail;
	private String contactPhone;
	private String contactMobile;
	private boolean isActive;
	private boolean isMail;
	private boolean isSms;
	private int userId;
	private String departmentName;
	
	public long getContactId() {
		return contactId;
	}
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getContactPersonName() {
		return contactPersonName;
	}
	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}
	public String getContactPersonDesignation() {
		return contactPersonDesignation;
	}
	public void setContactPersonDesignation(String contactPersonDesignation) {
		this.contactPersonDesignation = contactPersonDesignation;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getContactMobile() {
		return contactMobile;
	}
	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
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
	public boolean getIsMail() {
		return isMail;
	}
	public void setIsMail(boolean isMail) {
		this.isMail = isMail;
	}
	public boolean getIsSms() {
		return isSms;
	}
	public void setIsSms(boolean isSms) {
		this.isSms = isSms;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	
}
