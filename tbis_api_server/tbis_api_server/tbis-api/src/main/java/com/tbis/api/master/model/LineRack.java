package com.tbis.api.master.model;

public class LineRack {
	private int lineRackId;
	private int lineSpaceId;
	private String lineRackCode;
	private double customerStorageArea;
	private double goodStockAreaLength;
	private double goodStockAreaWidth;
	private double goodStockAreaHeight;
	private int noofPartStorageBays;
	private double areaM2;
	private double areaM3;
	private int startLine;
	private int endLine;
	private int startCol;
	private int endCol;
	
	private boolean isActive;
	private int userId;
	
	public int getLineRackId() {
		return lineRackId;
	}
	public void setLineRackId(int lineRackId) {
		this.lineRackId = lineRackId;
	}
	public int getLineSpaceId() {
		return lineSpaceId;
	}
	public void setLineSpaceId(int lineSpaceId) {
		this.lineSpaceId = lineSpaceId;
	}
	public String getLineRackCode() {
		return lineRackCode;
	}
	public void setLineRackCode(String lineRackCode) {
		this.lineRackCode = lineRackCode;
	}
	public double getCustomerStorageArea() {
		return customerStorageArea;
	}
	public void setCustomerStorageArea(double customerStorageArea) {
		this.customerStorageArea = customerStorageArea;
	}
	public double getGoodStockAreaLength() {
		return goodStockAreaLength;
	}
	public void setGoodStockAreaLength(double goodStockAreaLength) {
		this.goodStockAreaLength = goodStockAreaLength;
	}
	public double getGoodStockAreaWidth() {
		return goodStockAreaWidth;
	}
	public void setGoodStockAreaWidth(double goodStockAreaWidth) {
		this.goodStockAreaWidth = goodStockAreaWidth;
	}
	public int getNoofPartStorageBays() {
		return noofPartStorageBays;
	}
	public void setNoofPartStorageBays(int noofPartStorageBays) {
		this.noofPartStorageBays = noofPartStorageBays;
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
	public double getGoodStockAreaHeight() {
		return goodStockAreaHeight;
	}
	public void setGoodStockAreaHeight(double goodStockAreaHeight) {
		this.goodStockAreaHeight = goodStockAreaHeight;
	}
	public double getAreaM2() {
		return areaM2;
	}
	public void setAreaM2(double areaM2) {
		this.areaM2 = areaM2;
	}
	public double getAreaM3() {
		return areaM3;
	}
	public void setAreaM3(double areaM3) {
		this.areaM3 = areaM3;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public int getStartCol() {
		return startCol;
	}
	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}
	public int getEndCol() {
		return endCol;
	}
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}
	

}
