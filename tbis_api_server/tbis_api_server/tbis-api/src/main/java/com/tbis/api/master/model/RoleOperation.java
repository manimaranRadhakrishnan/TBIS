package com.tbis.api.master.model;

import java.io.Serializable;

public class RoleOperation implements Serializable{
	private int menuId;
	private int operationId;
	private int reportId;
	private String reportColumn;
	private String caption;
	private int menuReportId;
	private boolean selected;
	
	public int getMenuId() {
		return menuId;
	}
	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}
	public int getOperationId() {
		return operationId;
	}
	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}
	public int getReportId() {
		return reportId;
	}
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	public String getReportColumn() {
		return reportColumn;
	}
	public void setReportColumn(String reportColumn) {
		this.reportColumn = reportColumn;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public int getMenuReportId() {
		return menuReportId;
	}
	public void setMenuReportId(int menuReportId) {
		this.menuReportId = menuReportId;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
