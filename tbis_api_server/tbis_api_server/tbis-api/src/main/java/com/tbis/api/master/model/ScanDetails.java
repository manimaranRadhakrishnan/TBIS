package com.tbis.api.master.model;

import java.util.ArrayList;

public class ScanDetails {	
	private long scanId;
	private long asnId;
	private long transId;
	private String scanData;
	private String partNo;
	private String vendorCode;
	private String userLocation;
	private String qty;
	private String spq;
	private String binQty;
	private String requestedBinQty;
	private String serialNo;
	private int partId;
	private String partName;
	private int vendorId;
	private String vendorName;
	private int locationId;
	private String locationName;
	private int receivedQty;
	private long customerPartId;
	private String customerPartCode;
	private String subLocationShortCode;
	private String ackStatus;
	private String deliveryStatus;
	private String receiptStatus;
	private int incidentId;
	private String incidentName;
	private int asnStatus;
	private int loadingType;
	private int noOfPackingInPallet;
	private String customerScanCode;
	private String asnQty;
	private String asnBinQty;
	private int palletNumber;
	private String palletScanCode;
	private int processId;
	private int subLocationId;
	private String moveFromName;
	private String moveToName;
	private int packageId;
	private String packingShortName;
	private LineSpacePartConfig lineSpaceConfig;
	private boolean moveToOverflowLocation;
	private int pallets;
	private int remainingPalletBins;
	private String packingWeightWithPart;
	private String palletLength;
	private String palletWidth;
	private int noOfStack;
	private int repackQty;
	private int outwardBinQty;
	private int outwardBin;
	private int goodQty;
	private int badQty;
	private int holdQty;
	private int exclusiveClubNo;
	private int lineSpacePartConfigId;
	private int warehouseId;
	private int udcId;
	private int openingHoldQty;
	private long finalPartId;
	private int finalPartQty;
	private ArrayList<CustomerBarCode> customerBarCodes;
	private int openingGoodQty;
	private ArrayList<CustomerBarCode> excludedBarCodes;
	private ArrayList<String> barCodes;
	private int movedBinQty;
	private int movedQty;
	
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public String getUserLocation() {
		return userLocation;
	}
	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	
	public String getRequestedBinQty() {
		return requestedBinQty;
	}
	public void setRequestedBinQty(String requestedBinQty) {
		this.requestedBinQty = requestedBinQty;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	private int userId;
	
	public long getScanId() {
		return scanId;
	}
	public void setScanId(long scanId) {
		this.scanId = scanId;
	}
	public long getTransId() {
		return transId;
	}
	public void setTransId(long transId) {
		this.transId = transId;
	}
	public String getScanData() {
		return scanData;
	}
	public void setScanData(String scanData) {
		this.scanData = scanData;
	}
	public String getPartNo() {
		return partNo;
	}
	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getPartId() {
		return partId;
	}
	public void setPartId(int partId) {
		this.partId = partId;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	public int getVendorId() {
		return vendorId;
	}
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public int getReceivedQty() {
		return receivedQty;
	}
	public void setReceivedQty(int receivedQty) {
		this.receivedQty = receivedQty;
	}
	public long getCustomerPartId() {
		return customerPartId;
	}
	public void setCustomerPartId(long customerPartId) {
		this.customerPartId = customerPartId;
	}
	public String getAckStatus() {
		return ackStatus;
	}
	public void setAckStatus(String ackStatus) {
		this.ackStatus = ackStatus;
	}
	public String getDeliveryStatus() {
		return deliveryStatus;
	}
	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}
	public String getReceiptStatus() {
		return receiptStatus;
	}
	public void setReceiptStatus(String receiptStatus) {
		this.receiptStatus = receiptStatus;
	}
	public long getAsnId() {
		return asnId;
	}
	public void setAsnId(long asnId) {
		this.asnId = asnId;
	}
	public int getIncidentId() {
		return incidentId;
	}
	public void setIncidentId(int incidentId) {
		this.incidentId = incidentId;
	}
	public String getIncidentName() {
		return incidentName;
	}
	public void setIncidentName(String incidentName) {
		this.incidentName = incidentName;
	}
	public int getAsnStatus() {
		return asnStatus;
	}
	public void setAsnStatus(int asnStatus) {
		this.asnStatus = asnStatus;
	}
	public String getSpq() {
		return spq;
	}
	public void setSpq(String spq) {
		this.spq = spq;
	}
	public String getBinQty() {
		return binQty;
	}
	public void setBinQty(String binQty) {
		this.binQty = binQty;
	}
	public String getCustomerPartCode() {
		return customerPartCode;
	}
	public void setCustomerPartCode(String customerPartCode) {
		this.customerPartCode = customerPartCode;
	}
	public String getSubLocationShortCode() {
		return subLocationShortCode;
	}
	public void setSubLocationShortCode(String subLocationShortCode) {
		this.subLocationShortCode = subLocationShortCode;
	}
	public int getLoadingType() {
		return loadingType;
	}
	public void setLoadingType(int loadingType) {
		this.loadingType = loadingType;
	}
	public int getNoOfPackingInPallet() {
		return noOfPackingInPallet;
	}
	public void setNoOfPackingInPallet(int noOfPackingInPallet) {
		this.noOfPackingInPallet = noOfPackingInPallet;
	}
	public String getCustomerScanCode() {
		return customerScanCode;
	}
	public void setCustomerScanCode(String customerScanCode) {
		this.customerScanCode = customerScanCode;
	}
	public String getAsnQty() {
		return asnQty;
	}
	public void setAsnQty(String asnQty) {
		this.asnQty = asnQty;
	}
	public String getAsnBinQty() {
		return asnBinQty;
	}
	public void setAsnBinQty(String asnBinQty) {
		this.asnBinQty = asnBinQty;
	}
	public int getPalletNumber() {
		return palletNumber;
	}
	public void setPalletNumber(int palletNumber) {
		this.palletNumber = palletNumber;
	}
	public String getPalletScanCode() {
		return palletScanCode;
	}
	public void setPalletScanCode(String palletScanCode) {
		this.palletScanCode = palletScanCode;
	}
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public int getSubLocationId() {
		return subLocationId;
	}
	public void setSubLocationId(int subLocationId) {
		this.subLocationId = subLocationId;
	}
	public String getMoveFromName() {
		return moveFromName;
	}
	public void setMoveFromName(String moveFromName) {
		this.moveFromName = moveFromName;
	}
	public String getMoveToName() {
		return moveToName;
	}
	public void setMoveToName(String moveToName) {
		this.moveToName = moveToName;
	}
	public int getPackageId() {
		return packageId;
	}
	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}
	
	public String getPackingShortName() {
		return packingShortName;
	}
	public void setPackingShortName(String packingShortName) {
		this.packingShortName = packingShortName;
	}
	public LineSpacePartConfig getLineSpaceConfig() {
		return lineSpaceConfig;
	}
	public void setLineSpaceConfig(LineSpacePartConfig lineSpaceConfig) {
		this.lineSpaceConfig = lineSpaceConfig;
	}
	public boolean getMoveToOverflowLocation() {
		return moveToOverflowLocation;
	}
	public void setMoveToOverflowLocation(boolean moveToOverflowLocation) {
		this.moveToOverflowLocation = moveToOverflowLocation;
	}
	public int getPallets() {
		return pallets;
	}
	public void setPallets(int pallets) {
		this.pallets = pallets;
	}
	public int getRemainingPalletBins() {
		return remainingPalletBins;
	}
	public void setRemainingPalletBins(int remainingPalletBins) {
		this.remainingPalletBins = remainingPalletBins;
	}
	public String getPackingWeightWithPart() {
		return packingWeightWithPart;
	}
	public void setPackingWeightWithPart(String packingWeightWithPart) {
		this.packingWeightWithPart = packingWeightWithPart;
	}
	public String getPalletLength() {
		return palletLength;
	}
	public void setPalletLength(String palletLength) {
		this.palletLength = palletLength;
	}
	public String getPalletWidth() {
		return palletWidth;
	}
	public void setPalletWidth(String palletWidth) {
		this.palletWidth = palletWidth;
	}
	public int getNoOfStack() {
		return noOfStack;
	}
	public void setNoOfStack(int noOfStack) {
		this.noOfStack = noOfStack;
	}
	public int getRepackQty() {
		return repackQty;
	}
	public void setRepackQty(int repackQty) {
		this.repackQty = repackQty;
	}
	public int getOutwardBinQty() {
		return outwardBinQty;
	}
	public void setOutwardBinQty(int outwardBinQty) {
		this.outwardBinQty = outwardBinQty;
	}
	public int getOutwardBin() {
		return outwardBin;
	}
	public void setOutwardBin(int outwardBin) {
		this.outwardBin = outwardBin;
	}
	public int getGoodQty() {
		return goodQty;
	}
	public void setGoodQty(int goodQty) {
		this.goodQty = goodQty;
	}
	public int getBadQty() {
		return badQty;
	}
	public void setBadQty(int badQty) {
		this.badQty = badQty;
	}
	public int getHoldQty() {
		return holdQty;
	}
	public void setHoldQty(int holdQty) {
		this.holdQty = holdQty;
	}
	public int getExclusiveClubNo() {
		return exclusiveClubNo;
	}
	public void setExclusiveClubNo(int exclusiveClubNo) {
		this.exclusiveClubNo = exclusiveClubNo;
	}
	public int getLineSpacePartConfigId() {
		return lineSpacePartConfigId;
	}
	public void setLineSpacePartConfigId(int lineSpacePartConfigId) {
		this.lineSpacePartConfigId = lineSpacePartConfigId;
	}
	public int getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}
	public int getUdcId() {
		return udcId;
	}
	public void setUdcId(int udcId) {
		this.udcId = udcId;
	}
	public int getOpeningHoldQty() {
		return openingHoldQty;
	}
	public void setOpeningHoldQty(int openingHoldQty) {
		this.openingHoldQty = openingHoldQty;
	}
	public long getFinalPartId() {
		return finalPartId;
	}
	public void setFinalPartId(long finalPartId) {
		this.finalPartId = finalPartId;
	}
	public int getFinalPartQty() {
		return finalPartQty;
	}
	public void setFinalPartQty(int finalPartQty) {
		this.finalPartQty = finalPartQty;
	}
	public ArrayList<CustomerBarCode> getCustomerBarCodes() {
		return customerBarCodes;
	}
	public void setCustomerBarCodes(ArrayList<CustomerBarCode> customerBarCodes) {
		this.customerBarCodes = customerBarCodes;
	}
	public int getOpeningGoodQty() {
		return openingGoodQty;
	}
	public void setOpeningGoodQty(int openingGoodQty) {
		this.openingGoodQty = openingGoodQty;
	}
	public ArrayList<CustomerBarCode> getExcludedBarCodes() {
		return excludedBarCodes;
	}
	public void setExcludedBarCodes(ArrayList<CustomerBarCode> excludedBarCodes) {
		this.excludedBarCodes = excludedBarCodes;
	}
	public ArrayList<String> getBarCodes() {
		return barCodes;
	}
	public void setBarCodes(ArrayList<String> barCodes) {
		this.barCodes = barCodes;
	}
	public int getMovedBinQty() {
		return movedBinQty;
	}
	public void setMovedBinQty(int movedBinQty) {
		this.movedBinQty = movedBinQty;
	}
	public int getMovedQty() {
		return movedQty;
	}
	public void setMovedQty(int movedQty) {
		this.movedQty = movedQty;
	}
	
}
