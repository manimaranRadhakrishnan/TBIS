package com.tbis.api.master.model;

public class WmsMenus {
	private int menuId;
	private String menuName;
	private int menuParentId;
	private int internalOrExternal;
	private int menuDisplayOrder;
	private boolean isSystem;
	private int userId;
	
	public int getMenuId() {
		return menuId;
	}
	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public int getMenuParentId() {
		return menuParentId;
	}
	public void setMenuParentId(int menuParentId) {
		this.menuParentId = menuParentId;
	}
	public int getInternalOrExternal() {
		return internalOrExternal;
	}
	public void setInternalOrExternal(int internalOrExternal) {
		this.internalOrExternal = internalOrExternal;
	}
	public int getMenuDisplayOrder() {
		return menuDisplayOrder;
	}
	public void setMenuDisplayOrder(int menuDisplayOrder) {
		this.menuDisplayOrder = menuDisplayOrder;
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
