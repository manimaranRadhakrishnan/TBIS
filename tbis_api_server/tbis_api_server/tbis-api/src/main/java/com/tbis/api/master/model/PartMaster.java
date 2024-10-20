package com.tbis.api.master.model;

public class PartMaster {
	private long partId;
	private String partNo;
	private String partDescription;
	private int packingTypeId;
	private int spq;
	private int noOfStack;
	private int noOfPackingInPallet;
	private double packingHeight;
	private double packingWidth;
	private double packingLength;
	private double packingWeight;
	private double palletHeight;
	private double palletWidth;
	private double palletLength;
	private double palletWeight;
	private int dispatchType;
	private int loadingType;
	private int rePack;
	private double m2;
	private double m3;
	private boolean isActive;
	private int userId;
	private int unloadDockId;
	private int binQty;
	private int locationId;
	private boolean reqInspection;
	private int deliveryLocationId;
	private String plantName;
	private String unloadingDock;
	private int repackQty;
	
	private double packingWeightWithPart; 
	private double opackingWeight;
	private double opackingWeightWithPart; 
	private int onoOfStack; 
	private int onoOfPackingInPallet;
	private double opalletHeight;
	private double opalletWidth;
	private double opalletLength; 
	private double opalletWeight;
	private long finalPartId;
	private int opackingTypeId;
	private String customerPartNo; 
	private long customerId;
	private int pNoOfStack;
	private int oBinQty;
	

	public long getPartId() {
		return partId;
	}
	public void setPartId(long partId) {
		this.partId = partId;
	}
	public String getPartNo() {
		return partNo;
	}
	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	public String getPartDescription() {
		return partDescription;
	}
	public void setPartDescription(String partDescription) {
		this.partDescription = partDescription;
	}
	public int getPackingTypeId() {
		return packingTypeId;
	}
	public void setPackingTypeId(int packingTypeId) {
		this.packingTypeId = packingTypeId;
	}
	public int getSpq() {
		return spq;
	}
	public void setSpq(int spq) {
		this.spq = spq;
	}
	public int getNoOfStack() {
		return noOfStack;
	}
	public void setNoOfStack(int noOfStack) {
		this.noOfStack = noOfStack;
	}
	public int getNoOfPackingInPallet() {
		return noOfPackingInPallet;
	}
	public void setNoOfPackingInPallet(int noOfPackingInPallet) {
		this.noOfPackingInPallet = noOfPackingInPallet;
	}
	public double getPackingHeight() {
		return packingHeight;
	}
	public void setPackingHeight(double packingHeight) {
		this.packingHeight = packingHeight;
	}
	public double getPackingWidth() {
		return packingWidth;
	}
	public void setPackingWidth(double packingWidth) {
		this.packingWidth = packingWidth;
	}
	public double getPackingLength() {
		return packingLength;
	}
	public void setPackingLength(double packingLength) {
		this.packingLength = packingLength;
	}
	public double getPackingWeight() {
		return packingWeight;
	}
	public void setPackingWeight(double packingWeight) {
		this.packingWeight = packingWeight;
	}
	public double getPalletHeight() {
		return palletHeight;
	}
	public void setPalletHeight(double palletHeight) {
		this.palletHeight = palletHeight;
	}
	public double getPalletWidth() {
		return palletWidth;
	}
	public void setPalletWidth(double palletWidth) {
		this.palletWidth = palletWidth;
	}
	public double getPalletLength() {
		return palletLength;
	}
	public void setPalletLength(double palletLength) {
		this.palletLength = palletLength;
	}
	public double getPalletWeight() {
		return palletWeight;
	}
	public void setPalletWeight(double palletWeight) {
		this.palletWeight = palletWeight;
	}
	public int getDispatchType() {
		return dispatchType;
	}
	public void setDispatchType(int dispatchType) {
		this.dispatchType = dispatchType;
	}
	public int getLoadingType() {
		return loadingType;
	}
	public void setLoadingType(int loadingType) {
		this.loadingType = loadingType;
	}
	public int getRePack() {
		return rePack;
	}
	public void setRePack(int rePack) {
		this.rePack = rePack;
	}
	public double getM2() {
		return m2;
	}
	public void setM2(double m2) {
		this.m2 = m2;
	}
	public double getM3() {
		return m3;
	}
	public void setM3(double m3) {
		this.m3 = m3;
	}
	public boolean isActive() {
		return isActive;
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
	public int getUnloadDockId() {
		return unloadDockId;
	}
	public void setUnloadDockId(int unloadDockId) {
		this.unloadDockId = unloadDockId;
	}
	public int getBinQty() {
		return binQty;
	}
	public void setBinQty(int binQty) {
		this.binQty = binQty;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public boolean getReqInspection() {
		return reqInspection;
	}
	public void setReqInspection(boolean reqInspection) {
		this.reqInspection = reqInspection;
	}
	public int getDeliveryLocationId() {
		return deliveryLocationId;
	}
	public void setDeliveryLocationId(int deliveryLocationId) {
		this.deliveryLocationId = deliveryLocationId;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public String getUnloadingDock() {
		return unloadingDock;
	}
	public void setUnloadingDock(String unloadingDock) {
		this.unloadingDock = unloadingDock;
	}
	public int getRepackQty() {
		return repackQty;
	}
	public void setRepackQty(int repackQty) {
		this.repackQty = repackQty;
	}
	public double getPackingWeightWithPart() {
		return packingWeightWithPart;
	}
	public void setPackingWeightWithPart(double packingWeightWithPart) {
		this.packingWeightWithPart = packingWeightWithPart;
	}
	public double getOpackingWeight() {
		return opackingWeight;
	}
	public void setOpackingWeight(double opackingWeight) {
		this.opackingWeight = opackingWeight;
	}
	public double getOpackingWeightWithPart() {
		return opackingWeightWithPart;
	}
	public void setOpackingWeightWithPart(double opackingWeightWithPart) {
		this.opackingWeightWithPart = opackingWeightWithPart;
	}
	public int getOnoOfStack() {
		return onoOfStack;
	}
	public void setOnoOfStack(int onoOfStack) {
		this.onoOfStack = onoOfStack;
	}
	public int getOnoOfPackingInPallet() {
		return onoOfPackingInPallet;
	}
	public void setOnoOfPackingInPallet(int onoOfPackingInPallet) {
		this.onoOfPackingInPallet = onoOfPackingInPallet;
	}
	public double getOpalletHeight() {
		return opalletHeight;
	}
	public void setOpalletHeight(double opalletHeight) {
		this.opalletHeight = opalletHeight;
	}
	public double getOpalletWidth() {
		return opalletWidth;
	}
	public void setOpalletWidth(double opalletWidth) {
		this.opalletWidth = opalletWidth;
	}
	public double getOpalletLength() {
		return opalletLength;
	}
	public void setOpalletLength(double opalletLength) {
		this.opalletLength = opalletLength;
	}
	public double getOpalletWeight() {
		return opalletWeight;
	}
	public void setOpalletWeight(double opalletWeight) {
		this.opalletWeight = opalletWeight;
	}
	public long getFinalPartId() {
		return finalPartId;
	}
	public void setFinalPartId(long finalPartId) {
		this.finalPartId = finalPartId;
	}
	public int getOpackingTypeId() {
		return opackingTypeId;
	}
	public void setOpackingTypeId(int opackingTypeId) {
		this.opackingTypeId = opackingTypeId;
	}
	public String getCustomerPartNo() {
		return customerPartNo;
	}
	public void setCustomerPartNo(String customerPartNo) {
		this.customerPartNo = customerPartNo;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public int getpNoOfStack() {
		return pNoOfStack;
	}
	public void setpNoOfStack(int pNoOfStack) {
		this.pNoOfStack = pNoOfStack;
	}
	public int getoBinQty() {
		return oBinQty;
	}
	public void setoBinQty(int oBinQty) {
		this.oBinQty = oBinQty;
	}
	
	
}
