package com.tbis.api.master.model;

public class ApplicationType {	
	private int cwaId;
	private String cwaDesc;
	private Boolean cwaIsSystem;
	private Boolean cwaFormA;
	private Boolean cwaFormC;
	private Boolean cwaFormI;
	private Boolean cwaFormG;
	private Boolean isActive; 
	private int userId;
	
	public int getCwaId() {
		return cwaId;
	}
	public void setCwaId(int cwaId) {
		this.cwaId = cwaId;
	}
	public String getCwaDesc() {
		return cwaDesc;
	}
	public void setCwaDesc(String cwaDesc) {
		this.cwaDesc = cwaDesc;
	}
	public Boolean getCwaIsSystem() {
		return cwaIsSystem;
	}
	public void setCwaIsSystem(Boolean cwaIsSystem) {
		this.cwaIsSystem = cwaIsSystem;
	}
	public Boolean getCwaFormA() {
		return cwaFormA;
	}
	public void setCwaFormA(Boolean cwaFormA) {
		this.cwaFormA = cwaFormA;
	}
	public Boolean getCwaFormC() {
		return cwaFormC;
	}
	public void setCwaFormC(Boolean cwaFormC) {
		this.cwaFormC = cwaFormC;
	}
	public Boolean getCwaFormI() {
		return cwaFormI;
	}
	public void setCwaFormI(Boolean cwaFormI) {
		this.cwaFormI = cwaFormI;
	}
	public Boolean getCwaFormG() {
		return cwaFormG;
	}
	public void setCwaFormG(Boolean cwaFormG) {
		this.cwaFormG = cwaFormG;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
		
}
