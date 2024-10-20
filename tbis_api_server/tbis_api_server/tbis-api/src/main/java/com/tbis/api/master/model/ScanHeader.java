package com.tbis.api.master.model;

import java.util.ArrayList;

public class ScanHeader {
	private boolean isPallet;
	private int partCount;
	private LineSpacePartConfig lineSpaceConfig;
	private boolean moveToOverflowLocation;
	private ArrayList<ScanDetails> assemblyPartDetails;
	private ArrayList<ScanDetails> scannedPartDetails;
	private ScanDetails finalPart;
	public boolean getIsPallet() {
		return isPallet;
	}
	public void setIsPallet(boolean isPallet) {
		this.isPallet = isPallet;
	}
	public int getPartCount() {
		return partCount;
	}
	public void setPartCount(int partCount) {
		this.partCount = partCount;
	}
	public ArrayList<ScanDetails> getScannedPartDetails() {
		return scannedPartDetails;
	}
	public void setScannedPartDetails(ArrayList<ScanDetails> scannedPartDetails) {
		this.scannedPartDetails = scannedPartDetails;
	}
	public LineSpacePartConfig getLineSpaceConfig() {
		return lineSpaceConfig;
	}
	public void setLineSpaceConfig(LineSpacePartConfig lineSpaceConfig) {
		this.lineSpaceConfig = lineSpaceConfig;
	}
	public boolean isMoveToOverflowLocation() {
		return moveToOverflowLocation;
	}
	public void setMoveToOverflowLocation(boolean moveToOverflowLocation) {
		this.moveToOverflowLocation = moveToOverflowLocation;
	}
	public ArrayList<ScanDetails> getAssemblyPartDetails() {
		return assemblyPartDetails;
	}
	public void setAssemblyPartDetails(ArrayList<ScanDetails> assemblyPartDetails) {
		this.assemblyPartDetails = assemblyPartDetails;
	}
	public ScanDetails getFinalPart() {
		return finalPart;
	}
	public void setFinalPart(ScanDetails finalPart) {
		this.finalPart = finalPart;
	}
	
	
}	
