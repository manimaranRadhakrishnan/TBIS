package com.tbis.api.master.model;

public class WmsConfiguration {
	private int configId;
	private String configurationName;
	private String configurationValue;
	private int configurationValueType;
	private boolean isSystem;
	private int userId;
	
	public int getConfigId() {
		return configId;
	}
	public void setConfigId(int configId) {
		this.configId = configId;
	}
	public String getConfigurationName() {
		return configurationName;
	}
	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}
	public String getConfigurationValue() {
		return configurationValue;
	}
	public void setConfigurationValue(String configurationValue) {
		this.configurationValue = configurationValue;
	}
	public int getConfigurationValueType() {
		return configurationValueType;
	}
	public void setConfigurationValueType(int configurationValueType) {
		this.configurationValueType = configurationValueType;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
