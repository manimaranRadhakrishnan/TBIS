package com.tbis.api.master.model;

public class PickWorkFlowInput {
	private long pickListId;
	private long pickListDetailId;
	private int pickListWorkFlowId;
	private int statusId;
	private int workFlowHandleBy;
	private String notes;
	private int userId;
	
	public long getPickListId() {
		return pickListId;
	}
	public void setPickListId(long pickListId) {
		this.pickListId = pickListId;
	}
	public long getPickListDetailId() {
		return pickListDetailId;
	}
	public void setPickListDetailId(long pickListDetailId) {
		this.pickListDetailId = pickListDetailId;
	}
	public int getPickListWorkFlowId() {
		return pickListWorkFlowId;
	}
	public void setPickListWorkFlowId(int pickListWorkFlowId) {
		this.pickListWorkFlowId = pickListWorkFlowId;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public int getWorkFlowHandleBy() {
		return workFlowHandleBy;
	}
	public void setWorkFlowHandleBy(int workFlowHandleBy) {
		this.workFlowHandleBy = workFlowHandleBy;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
