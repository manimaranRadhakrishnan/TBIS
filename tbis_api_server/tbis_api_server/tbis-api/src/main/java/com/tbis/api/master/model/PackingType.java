package com.tbis.api.master.model;

public class PackingType {
	private int packingTypeId;
	private String packingName;
	private String packingShortName;
	private int packingHeight;
	private int packingWidth;
	private int packingLength;
	private int packingWeight;
	private int m2;
	private int m3;
	private boolean isActive;
	private int userId;
	private String packingCategory;
	private String packingColor;
	
	public int getPackingTypeId() {
		return packingTypeId;
	}
	public void setPackingTypeId(int packingTypeId) {
		this.packingTypeId = packingTypeId;
	}
	public String getPackingName() {
		return packingName;
	}
	public void setPackingName(String packingName) {
		this.packingName = packingName;
	}
	public String getPackingShortName() {
		return packingShortName;
	}
	public void setPackingShortName(String packingShortName) {
		this.packingShortName = packingShortName;
	}
	public int getPackingHeight() {
		return packingHeight;
	}
	public void setPackingHeight(int packingHeight) {
		this.packingHeight = packingHeight;
	}
	public int getPackingWidth() {
		return packingWidth;
	}
	public void setPackingWidth(int packingWidth) {
		this.packingWidth = packingWidth;
	}
	public int getPackingLength() {
		return packingLength;
	}
	public void setPackingLength(int packingLength) {
		this.packingLength = packingLength;
	}
	public int getPackingWeight() {
		return packingWeight;
	}
	public void setPackingWeight(int packingWeight) {
		this.packingWeight = packingWeight;
	}
	public int getM2() {
		return m2;
	}
	public void setM2(int m2) {
		this.m2 = m2;
	}
	public int getM3() {
		return m3;
	}
	public void setM3(int m3) {
		this.m3 = m3;
	}
	public boolean isActive() {
		return this.isActive;
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
	public String getPackingCategory() {
		return packingCategory;
	}
	public void setPackingCategory(String packingCategory) {
		this.packingCategory = packingCategory;
	}
	public String getPackingColor() {
		return packingColor;
	}
	public void setPackingColor(String packingColor) {
		this.packingColor = packingColor;
	}
	
}
