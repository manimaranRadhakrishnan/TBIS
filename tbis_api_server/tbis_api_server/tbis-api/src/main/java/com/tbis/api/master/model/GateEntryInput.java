package com.tbis.api.master.model;

public class GateEntryInput {	
	private long asnId;
	private int asnStatusId;
	private String vechicleNo;
	private int userId;
	private String lastAsnStatusId;
	private boolean transitLoad;
	
	public long getAsnId() {
		return asnId;
	}
	public void setAsnId(long asnId) {
		this.asnId = asnId;
	}
	public int getAsnStatusId() {
		return asnStatusId;
	}
	public void setAsnStatusId(int asnStatusId) {
		this.asnStatusId = asnStatusId;
	}
	public String getVechicleNo() {
		return vechicleNo;
	}
	public void setVechicleNo(String vechicleNo) {
		this.vechicleNo = vechicleNo;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getLastAsnStatusId() {
		return lastAsnStatusId;
	}
	public void setLastAsnStatusId(String lastAsnStatusId) {
		this.lastAsnStatusId = lastAsnStatusId;
	}
	public boolean getTransitLoad() {
		return transitLoad;
	}
	public void setTransitLoad(boolean transitLoad) {
		this.transitLoad = transitLoad;
	}
	
}
