package com.tbis.api.master.model;

public class LoadingDetails {
	private long pickListId;
	private int loadingSupervisorId;
	private int docHandoverId;
	private int docAuditId;
	private int userId;
	
	public long getPickListId() {
		return pickListId;
	}
	public void setPickListId(long pickListId) {
		this.pickListId = pickListId;
	}
	public int getLoadingSupervisorId() {
		return loadingSupervisorId;
	}
	public void setLoadingSupervisorId(int loadingSupervisorId) {
		this.loadingSupervisorId = loadingSupervisorId;
	}
	public int getDocHandoverId() {
		return docHandoverId;
	}
	public void setDocHandoverId(int docHandoverId) {
		this.docHandoverId = docHandoverId;
	}
	public int getDocAuditId() {
		return docAuditId;
	}
	public void setDocAuditId(int docAuditId) {
		this.docAuditId = docAuditId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
	
}
