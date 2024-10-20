package com.tbis.api.master.model;

import java.util.ArrayList;

public class PickListDetail {
	private long pickListiDetailId;
	private long pickListId;
	private String kanbanNo;
	private long partId;
	private String partNo;
	private String partName;
	private String customerPartCode;
	private String supplyDate;
	private String supplyTime;
	private String unLoadingDoc;
	private String usageLocation;
	private int spq;
	private int qty;
	private int perBinQty;
	private int qtyReceived;
	private int noOfPackackes;
	private int stagIngState;
	private int userId;
	private int binQty;
	private String scanCode;
	private int lineSpacePartConfigId;
	private long stockMovementDetailId;
	private LineSpacePartConfig lineSpacePartConfig;
	private ArrayList<ScanDetails> childParts;
	private int exclusiveClubNo;
	private boolean updateStockDetail;
	private int availableBin;
	
	public long getPickListiDetailId() {
		return pickListiDetailId;
	}
	public void setPickListiDetailId(long pickListiDetailId) {
		this.pickListiDetailId = pickListiDetailId;
	}
	public long getPickListId() {
		return pickListId;
	}
	public void setPickListId(long pickListId) {
		this.pickListId = pickListId;
	}
	public String getKanbanNo() {
		return kanbanNo;
	}
	public void setKanbanNo(String kanbanNo) {
		this.kanbanNo = kanbanNo;
	}
	public long getPartId() {
		return partId;
	}
	public void setPartId(long partId) {
		this.partId = partId;
	}
	public String getSupplyDate() {
		return supplyDate;
	}
	public void setSupplyDate(String supplyDate) {
		this.supplyDate = supplyDate;
	}
	public String getSupplyTime() {
		return supplyTime;
	}
	public void setSupplyTime(String supplyTime) {
		this.supplyTime = supplyTime;
	}
	public String getUnLoadingDoc() {
		return unLoadingDoc;
	}
	public void setUnLoadingDoc(String unLoadingDoc) {
		this.unLoadingDoc = unLoadingDoc;
	}
	public String getUsageLocation() {
		return usageLocation;
	}
	public void setUsageLocation(String usageLocation) {
		this.usageLocation = usageLocation;
	}
	public int getSpq() {
		return spq;
	}
	public void setSpq(int spq) {
		this.spq = spq;
	}
	public int getPerBinQty() {
		return perBinQty;
	}
	public void setPerBinQty(int perBinQty) {
		this.perBinQty = perBinQty;
	}
	public int getQtyReceived() {
		return qtyReceived;
	}
	public void setQtyReceived(int qtyReceived) {
		this.qtyReceived = qtyReceived;
	}
	public int getNoOfPackackes() {
		return noOfPackackes;
	}
	public void setNoOfPackackes(int noOfPackackes) {
		this.noOfPackackes = noOfPackackes;
	}
	public int getStagIngState() {
		return stagIngState;
	}
	public void setStagIngState(int stagIngState) {
		this.stagIngState = stagIngState;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getBinQty() {
		return binQty;
	}
	public void setBinQty(int binQty) {
		this.binQty = binQty;
	}
	public String getPartNo() {
		return partNo;
	}
	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	public String getCustomerPartCode() {
		return customerPartCode;
	}
	public void setCustomerPartCode(String customerPartCode) {
		this.customerPartCode = customerPartCode;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public LineSpacePartConfig getLineSpacePartConfig() {
		return lineSpacePartConfig;
	}
	public void setLineSpacePartConfig(LineSpacePartConfig lineSpacePartConfig) {
		this.lineSpacePartConfig = lineSpacePartConfig;
	}
	public String getScanCode() {
		return scanCode;
	}
	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}
	public int getLineSpacePartConfigId() {
		return lineSpacePartConfigId;
	}
	public void setLineSpacePartConfigId(int lineSpacePartConfigId) {
		this.lineSpacePartConfigId = lineSpacePartConfigId;
	}
	public long getStockMovementDetailId() {
		return stockMovementDetailId;
	}
	public void setStockMovementDetailId(long stockMovementDetailId) {
		this.stockMovementDetailId = stockMovementDetailId;
	}
	public ArrayList<ScanDetails> getChildParts() {
		return childParts;
	}
	public void setChildParts(ArrayList<ScanDetails> childParts) {
		this.childParts = childParts;
	}
	public int getExclusiveClubNo() {
		return exclusiveClubNo;
	}
	public void setExclusiveClubNo(int exclusiveClubNo) {
		this.exclusiveClubNo = exclusiveClubNo;
	}
	public boolean getUpdateStockDetail() {
		return updateStockDetail;
	}
	public void setUpdateStockDetail(boolean updateStockDetail) {
		this.updateStockDetail = updateStockDetail;
	}
	public int getAvailableBin() {
		return availableBin;
	}
	public void setAvailableBin(int availableBin) {
		this.availableBin = availableBin;
	}
	
}
