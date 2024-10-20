package com.tbis.api.master.model;

import java.util.ArrayList;

public class AsnMaster {	
	private long asnId;
	private String asnNo;
	private long customerId;
	private String supplyDate;
	private String supplyTime;
	private int asnStatus;
	private String vechicleNo;
	private String driverName;
	private String driverMobile;
	private String ewayBillNo;
	private String invoiceNo;
	private Boolean directorTransit;
	private long transitasnId;
	private String gateInDateTime;
	private int userId;
	private int cardsIssued;
	private int cardsReceived;
	private int cardsDispatched;
	private int cardsAcknowledged;
	private String returnPack;
	private int returnPackQty;
	private int unLoadingDocId;
	private int subloactionId;
	private int warehouseId;
	private int loactionId;
	private int transitlocationId;
	private String dockShortName;
	private String sublocationShortName;
	private String warehouseShortName;
	private String locationShortName;
	private String dockName;
	private String customerName;
	private String customerCode;
	private int fromProcessId;
	private int toProcessId;
	private int employeeId;
	private int primaryDcokid;
	private String deliveryNoteNo;
	private int cardsConfirmed;
	private int dispatchConfirmed;
	private int vehicleTypeId;
	private String rgpNo;
	private double filledSize;
	private double filledCapacity;
	private int stockMovedFromDock;
	private int tripType;
	private int noOfPallet;
	private long tripId;
	private String gateEntryComment;
	private int gateEntryStatus;
	private int partsAdd;
	private String scwComments;
	private int inboundId;
	private String containerNo;
	private String inboundNo;
	private int manualPalletNumber;
	
	private ArrayList<ScanDetails> asnDetail;
	private ArrayList<String> scannedParts;
	private ArrayList<ASNBinTag> asnBinTag;
	ScanDetails finalPartDetail;
	
	public long getAsnId() {
		return asnId;
	}
	public void setAsnId(long asnId) {
		this.asnId = asnId;
	}
	public String getAsnNo() {
		return asnNo;
	}
	public void setAsnNo(String asnNo) {
		this.asnNo = asnNo;
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
	public int getUnLoadingDocId() {
		return unLoadingDocId;
	}
	public void setUnLoadingDocId(int unLoadingDocId) {
		this.unLoadingDocId = unLoadingDocId;
	}
	public int getAsnStatus() {
		return asnStatus;
	}
	public void setAsnStatus(int asnStatus) {
		this.asnStatus = asnStatus;
	}
	public String getVechicleNo() {
		return vechicleNo;
	}
	public void setVechicleNo(String vechicleNo) {
		this.vechicleNo = vechicleNo;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverMobile() {
		return driverMobile;
	}
	public void setDriverMobile(String driverMobile) {
		this.driverMobile = driverMobile;
	}
	public String getEwayBillNo() {
		return ewayBillNo;
	}
	public void setEwayBillNo(String ewayBillNo) {
		this.ewayBillNo = ewayBillNo;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public Boolean getDirectorTransit() {
		return directorTransit;
	}
	public void setDirectorTransit(Boolean directorTransit) {
		this.directorTransit = directorTransit;
	}
	public long getTransitasnId() {
		return transitasnId;
	}
	public void setTransitasnId(long transitasnId) {
		this.transitasnId = transitasnId;
	}
	public String getGateInDateTime() {
		return gateInDateTime;
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setGateIndatetime(String gateInDateTime) {
		this.gateInDateTime = gateInDateTime;
	}
	public ArrayList<ScanDetails> getAsnDetail() {
		return asnDetail;
	}
	public void setAsnDetail(ArrayList<ScanDetails> asnDetail) {
		this.asnDetail = asnDetail;
	}
	public void setGateInDateTime(String gateInDateTime) {
		this.gateInDateTime = gateInDateTime;
	}
	public int getCardsIssued() {
		return cardsIssued;
	}
	public void setCardsIssued(int cardsIssued) {
		this.cardsIssued = cardsIssued;
	}
	public int getCardsReceived() {
		return cardsReceived;
	}
	public void setCardsReceived(int cardsReceived) {
		this.cardsReceived = cardsReceived;
	}
	public int getCardsDispatched() {
		return cardsDispatched;
	}
	public void setCardsDispatched(int cardsDispatched) {
		this.cardsDispatched = cardsDispatched;
	}
	public int getCardsAcknowledged() {
		return cardsAcknowledged;
	}
	public void setCardsAcknowledged(int cardsAcknowledged) {
		this.cardsAcknowledged = cardsAcknowledged;
	}
	public String getReturnPack() {
		return returnPack;
	}
	public void setReturnPack(String returnPack) {
		this.returnPack = returnPack;
	}
	public int getReturnPackQty() {
		return returnPackQty;
	}
	public void setReturnPackQty(int returnPackQty) {
		this.returnPackQty = returnPackQty;
	}
	public int getSubloactionId() {
		return subloactionId;
	}
	public void setSubloactionId(int subloactionId) {
		this.subloactionId = subloactionId;
	}
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public int getLoactionId() {
		return loactionId;
	}
	public void setLoactionId(int loactionId) {
		this.loactionId = loactionId;
	}
	public String getDockShortName() {
		return dockShortName;
	}
	public void setDockShortName(String dockShortName) {
		this.dockShortName = dockShortName;
	}
	public String getSublocationShortName() {
		return sublocationShortName;
	}
	public void setSublocationShortName(String sublocationShortName) {
		this.sublocationShortName = sublocationShortName;
	}
	public String getWarehouseShortName() {
		return warehouseShortName;
	}
	public void setWarehouseShortName(String warehouseShortName) {
		this.warehouseShortName = warehouseShortName;
	}
	public String getLocationShortName() {
		return locationShortName;
	}
	public void setLocationShortName(String locationShortName) {
		this.locationShortName = locationShortName;
	}
	public int getTransitlocationId() {
		return transitlocationId;
	}
	public void setTransitlocationId(int transitlocationId) {
		this.transitlocationId = transitlocationId;
	}
	public String getDockName() {
		return dockName;
	}
	public void setDockName(String dockName) {
		this.dockName = dockName;
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
	public int getFromProcessId() {
		return fromProcessId;
	}
	public void setFromProcessId(int fromProcessId) {
		this.fromProcessId = fromProcessId;
	}
	public int getToProcessId() {
		return toProcessId;
	}
	public void setToProcessId(int toProcessId) {
		this.toProcessId = toProcessId;
	}
	public int getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}
	public ArrayList<String> getScannedParts() {
		return scannedParts;
	}
	public void setScannedParts(ArrayList<String> scannedParts) {
		this.scannedParts = scannedParts;
	}
	public int getPrimaryDcokid() {
		return primaryDcokid;
	}
	public void setPrimaryDcokid(int primaryDcokid) {
		this.primaryDcokid = primaryDcokid;
	}
	public String getDeliveryNoteNo() {
		return deliveryNoteNo;
	}
	public void setDeliveryNoteNo(String deliveryNoteNo) {
		this.deliveryNoteNo = deliveryNoteNo;
	}
	public int getCardsConfirmed() {
		return cardsConfirmed;
	}
	public void setCardsConfirmed(int cardsConfirmed) {
		this.cardsConfirmed = cardsConfirmed;
	}
	public int getDispatchConfirmed() {
		return dispatchConfirmed;
	}
	public void setDispatchConfirmed(int dispatchConfirmed) {
		this.dispatchConfirmed = dispatchConfirmed;
	}
	public ArrayList<ASNBinTag> getAsnBinTag() {
		return asnBinTag;
	}
	public void setAsnBinTag(ArrayList<ASNBinTag> asnBinTag) {
		this.asnBinTag = asnBinTag;
	}
	public int getVehicleTypeId() {
		return vehicleTypeId;
	}
	public void setVehicleTypeId(int vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}
	public String getRgpNo() {
		return rgpNo;
	}
	public void setRgpNo(String rgpNo) {
		this.rgpNo = rgpNo;
	}
	public double getFilledSize() {
		return filledSize;
	}
	public void setFilledSize(double filledSize) {
		this.filledSize = filledSize;
	}
	public double getFilledCapacity() {
		return filledCapacity;
	}
	public void setFilledCapacity(double filledCapacity) {
		this.filledCapacity = filledCapacity;
	}
	public int getStockMovedFromDock() {
		return stockMovedFromDock;
	}
	public void setStockMovedFromDock(int stockMovedFromDock) {
		this.stockMovedFromDock = stockMovedFromDock;
	}	
	public int getTripType() {
		return tripType;
	}
	public void setTripType(int tripType) {
		this.tripType = tripType;
	}
	public int getNoOfPallet() {
		return noOfPallet;
	}
	public void setNoOfPallet(int noOfPallet) {
		this.noOfPallet = noOfPallet;
	}
	public long getTripId() {
		return tripId;
	}
	public void setTripId(long tripId) {
		this.tripId = tripId;
	}
	public String getGateEntryComment() {
		return gateEntryComment;
	}
	public void setGateEntryComment(String gateEntryComment) {
		this.gateEntryComment = gateEntryComment;
	}
	public int getGateEntryStatus() {
		return gateEntryStatus;
	}
	public void setGateEntryStatus(int gateEntryStatus) {
		this.gateEntryStatus = gateEntryStatus;
	}
	public int getPartsAdd() {
		return partsAdd;
	}
	public void setPartsAdd(int partsAdd) {
		this.partsAdd = partsAdd;
	}
	public ScanDetails getFinalPartDetail() {
		return finalPartDetail;
	}
	public void setFinalPartDetail(ScanDetails finalPartDetail) {
		this.finalPartDetail = finalPartDetail;
	}
	public String getScwComments() {
		return scwComments;
	}
	public void setScwComments(String scwComments) {
		this.scwComments = scwComments;
	}
	public int getInboundId() {
		return inboundId;
	}
	public void setInboundId(int inboundId) {
		this.inboundId = inboundId;
	}
	public String getContainerNo() {
		return containerNo;
	}
	public void setContainerNo(String containerNo) {
		this.containerNo = containerNo;
	}
	public String getInboundNo() {
		return inboundNo;
	}
	public void setInboundNo(String inboundNo) {
		this.inboundNo = inboundNo;
	}
	public int getManualPalletNumber() {
		return manualPalletNumber;
	}
	public void setManualPalletNumber(int manualPalletNumber) {
		this.manualPalletNumber = manualPalletNumber;
	}
	

}
