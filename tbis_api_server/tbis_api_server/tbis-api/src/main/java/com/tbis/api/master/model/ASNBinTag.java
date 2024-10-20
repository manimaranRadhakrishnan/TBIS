package com.tbis.api.master.model;

public class ASNBinTag {	
	private long asnId;
	private long asnDetailId;
	private int processId;
	private int palletNo;
	private int partId;
	private String partNo;
	private String partName;
	private String customerPartCode;
	private int asnBinQty;
	private String packingShortName;
	private int loadingType;
	
	public long getAsnId() {
		return asnId;
	}
	public void setAsnId(long asnId) {
		this.asnId = asnId;
	}
	public long getAsnDetailId() {
		return asnDetailId;
	}
	public void setAsnDetailId(long asnDetailId) {
		this.asnDetailId = asnDetailId;
	}
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public int getPalletNo() {
		return palletNo;
	}
	public void setPalletNo(int palletNo) {
		this.palletNo = palletNo;
	}
	public int getPartId() {
		return partId;
	}
	public void setPartId(int partId) {
		this.partId = partId;
	}
	public String getPartNo() {
		return partNo;
	}
	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	public String getCustomerPartCode() {
		return customerPartCode;
	}
	public void setCustomerPartCode(String customerPartCode) {
		this.customerPartCode = customerPartCode;
	}
	
	public int getAsnBinQty() {
		return asnBinQty;
	}
	public void setAsnBinQty(int asnBinQty) {
		this.asnBinQty = asnBinQty;
	}
	public int getLoadingType() {
		return loadingType;
	}
	public void setLoadingType(int loadingType) {
		this.loadingType = loadingType;
	}
	public String getPackingShortName() {
		return packingShortName;
	}
	public void setPackingShortName(String packingShortName) {
		this.packingShortName = packingShortName;
	}
			
}
