package com.tbis.api.master.model;

import java.util.ArrayList;

public class MailConfigs {	
	
	private int mailTemplateId;
	private String mailTemplatename;	
	private String mailSubject;		
	private String mailContent;
	private int attachData;
	private boolean isActive;
	private int userId;
	
	public int getMailTemplateId() {
		return mailTemplateId;
	}
	public void setMailTemplateId(int mailTemplateId) {
		this.mailTemplateId = mailTemplateId;
	}
	public String getMailTemplatename() {
		return mailTemplatename;
	}
	public void setMailTemplatename(String mailTemplatename) {
		this.mailTemplatename = mailTemplatename;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	public String getMailContent() {
		return mailContent;
	}
	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}
	public int getAttachData() {
		return attachData;
	}
	public void setAttachData(int attachData) {
		this.attachData = attachData;
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
