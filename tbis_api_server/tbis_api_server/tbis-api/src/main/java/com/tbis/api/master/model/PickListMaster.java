package com.tbis.api.master.model;

import java.util.ArrayList;

public class PickListMaster {
	private long pickListId;
	private String scheduleNo;
	private long customerId;
	private String supplyDate;
	private String supplyTime;
	private int unloadingDoc;
	private int usageLocation;
	private int statusId;
	private int userId;
	private int pickedById;
	private String pickedByName;
	private int stagedById;
	private String stagedByName;
	private int kanbanAttachedById;
	private String kanbanAttachedByName;
	private int kanbanTapingById;
	private String kanbanTapingByName;
	private int scanById;
	private String scanByName;
	private int loadingSupervisorById;
	private String loadingSupervisorByName;
	private int docHandoverById;
	private String docHandoverByName;
	private int docAuditById;
	private String docAuditByName;
	private int vehicleAssignedById;
	private String vehicleAssignedByName;
	private String vehicleNo;
	private int subLocationId;
	private int warehouseId;
	private int locationId;
	private int processStatusId;
	private String customerName;
	private String customerCode;
	private long parentPickListId;
	
	private ArrayList<PickListDetail> pickListDetail;
	
	public long getPickListId() {
		return pickListId;
	}
	public void setPickListId(long pickListId) {
		this.pickListId = pickListId;
	}
	public String getScheduleNo() {
		return scheduleNo;
	}
	public void setScheduleNo(String scheduleNo) {
		this.scheduleNo = scheduleNo;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
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
	
	public int getUnloadingDoc() {
		return unloadingDoc;
	}
	public void setUnloadingDoc(int unloadingDoc) {
		this.unloadingDoc = unloadingDoc;
	}
	public int getUsageLocation() {
		return usageLocation;
	}
	public void setUsageLocation(int usageLocation) {
		this.usageLocation = usageLocation;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public ArrayList<PickListDetail> getPickListDetail() {
		return pickListDetail;
	}
	public void setPickListDetail(ArrayList<PickListDetail> pickListDetail) {
		this.pickListDetail = pickListDetail;
	}
	public int getPickedById() {
		return pickedById;
	}
	public void setPickedById(int pickedById) {
		this.pickedById = pickedById;
	}
	public String getPickedByName() {
		return pickedByName;
	}
	public void setPickedByName(String pickedByName) {
		this.pickedByName = pickedByName;
	}
	public int getStagedById() {
		return stagedById;
	}
	public void setStagedById(int stagedById) {
		this.stagedById = stagedById;
	}
	public String getStagedByName() {
		return stagedByName;
	}
	public void setStagedByName(String stagedByName) {
		this.stagedByName = stagedByName;
	}
	public int getKanbanAttachedById() {
		return kanbanAttachedById;
	}
	public void setKanbanAttachedById(int kanbanAttachedById) {
		this.kanbanAttachedById = kanbanAttachedById;
	}
	public String getKanbanAttachedByName() {
		return kanbanAttachedByName;
	}
	public void setKanbanAttachedByName(String kanbanAttachedByName) {
		this.kanbanAttachedByName = kanbanAttachedByName;
	}
	public int getKanbanTapingById() {
		return kanbanTapingById;
	}
	public void setKanbanTapingById(int kanbanTapingById) {
		this.kanbanTapingById = kanbanTapingById;
	}
	public String getKanbanTapingByName() {
		return kanbanTapingByName;
	}
	public void setKanbanTapingByName(String kanbanTapingByName) {
		this.kanbanTapingByName = kanbanTapingByName;
	}
	public int getScanById() {
		return scanById;
	}
	public void setScanById(int scanById) {
		this.scanById = scanById;
	}
	public String getScanByName() {
		return scanByName;
	}
	public void setScanByName(String scanByName) {
		this.scanByName = scanByName;
	}
	public int getLoadingSupervisorById() {
		return loadingSupervisorById;
	}
	public void setLoadingSupervisorById(int loadingSupervisorById) {
		this.loadingSupervisorById = loadingSupervisorById;
	}
	public String getLoadingSupervisorByName() {
		return loadingSupervisorByName;
	}
	public void setLoadingSupervisorByName(String loadingSupervisorByName) {
		this.loadingSupervisorByName = loadingSupervisorByName;
	}
	public int getDocHandoverById() {
		return docHandoverById;
	}
	public void setDocHandoverById(int docHandoverById) {
		this.docHandoverById = docHandoverById;
	}
	public String getDocHandoverByName() {
		return docHandoverByName;
	}
	public void setDocHandoverByName(String docHandoverByName) {
		this.docHandoverByName = docHandoverByName;
	}
	public int getDocAuditById() {
		return docAuditById;
	}
	public void setDocAuditById(int docAuditById) {
		this.docAuditById = docAuditById;
	}
	public String getDocAuditByName() {
		return docAuditByName;
	}
	public void setDocAuditByName(String docAuditByName) {
		this.docAuditByName = docAuditByName;
	}
	public int getVehicleAssignedById() {
		return vehicleAssignedById;
	}
	public void setVehicleAssignedById(int vehicleAssignedById) {
		this.vehicleAssignedById = vehicleAssignedById;
	}
	public String getVehicleAssignedByName() {
		return vehicleAssignedByName;
	}
	public void setVehicleAssignedByName(String vehicleAssignedByName) {
		this.vehicleAssignedByName = vehicleAssignedByName;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public int getSubLocationId() {
		return subLocationId;
	}
	public void setSubLocationId(int subLocationId) {
		this.subLocationId = subLocationId;
	}
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public int getProcessStatusId() {
		return processStatusId;
	}
	public void setProcessStatusId(int processStatusId) {
		this.processStatusId = processStatusId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public long getParentPickListId() {
		return parentPickListId;
	}
	public void setParentPickListId(long parentPickListId) {
		this.parentPickListId = parentPickListId;
	}
	
	
}
