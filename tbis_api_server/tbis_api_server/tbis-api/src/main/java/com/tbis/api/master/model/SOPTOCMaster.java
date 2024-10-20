package com.tbis.api.master.model;

public class SOPTOCMaster {	
	private long sopTocid;
	private long sopId;
	private int sopTopicParentid;
	private int sopTopicOrder;
	private int sopTypeid;
	private int sopSourceTypeid;
	private int warehouseId;
	private String sopSourceUrl;
	private String sopTopicName;
	private String sopTopicNameLocal;
	private String sopSourceUrlData;
	private String documentPath;
	private boolean isActive;
	private int userId;
	
	public long getSopTocid() {
		return sopTocid;
	}
	public void setSopTocid(long sopTocid) {
		this.sopTocid = sopTocid;
	}
	public long getSopId() {
		return sopId;
	}
	public void setSopId(long sopId) {
		this.sopId = sopId;
	}
	public int getSopTopicParentid() {
		return sopTopicParentid;
	}
	public void setSopTopicParentid(int sopTopicParentid) {
		this.sopTopicParentid = sopTopicParentid;
	}
	public int getSopTopicOrder() {
		return sopTopicOrder;
	}
	public void setSopTopicOrder(int sopTopicOrder) {
		this.sopTopicOrder = sopTopicOrder;
	}
	public int getSopTypeid() {
		return sopTypeid;
	}
	public void setSopTypeid(int sopTypeid) {
		this.sopTypeid = sopTypeid;
	}
	public int getSopSourceTypeid() {
		return sopSourceTypeid;
	}
	public void setSopSourceTypeid(int sopSourceTypeid) {
		this.sopSourceTypeid = sopSourceTypeid;
	}
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getSopSourceUrl() {
		return sopSourceUrl;
	}
	public void setSopSourceUrl(String sopSourceUrl) {
		this.sopSourceUrl = sopSourceUrl;
	}
	public String getSopTopicNameLocal() {
		return sopTopicNameLocal;
	}
	public void setSopTopicNameLocal(String sopTopicNameLocal) {
		this.sopTopicNameLocal = sopTopicNameLocal;
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
	public String getSopTopicName() {
		return sopTopicName;
	}
	public void setSopTopicName(String sopTopicName) {
		this.sopTopicName = sopTopicName;
	}
	public String getSopSourceUrlData() {
		return sopSourceUrlData;
	}
	public void setSopSourceUrlData(String sopSourceUrlData) {
		this.sopSourceUrlData = sopSourceUrlData;
	}
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	
}
