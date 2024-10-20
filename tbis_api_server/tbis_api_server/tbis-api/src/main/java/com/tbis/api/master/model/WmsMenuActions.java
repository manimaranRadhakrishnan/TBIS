package com.tbis.api.master.model;

public class WmsMenuActions {
	private int menuActionId;
	private String menuId;
	private int actionId;
	private int actionAvail;
	private boolean isSystem;
	private int userId;
	
	public int getMenuActionId() {
		return menuActionId;
	}
	public void setMenuActionId(int menuActionId) {
		this.menuActionId = menuActionId;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public int getActionId() {
		return actionId;
	}
	public void setActionId(int actionId) {
		this.actionId = actionId;
	}
	public int getActionAvail() {
		return actionAvail;
	}
	public void setActionAvail(int actionAvail) {
		this.actionAvail = actionAvail;
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
