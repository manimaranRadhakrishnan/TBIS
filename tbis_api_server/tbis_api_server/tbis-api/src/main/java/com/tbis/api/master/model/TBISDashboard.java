package com.tbis.api.master.model;

import java.util.ArrayList;

public class TBISDashboard {	
	private LongHaul longHaul;
	private LongHaul shortHaul;
	private LongHaul asnData;
	private LongHaul deliveryData;
	private ArrayList<OverFlowData> overflowData;
	public LongHaul getLongHaul() {
		return longHaul;
	}
	public void setLongHaul(LongHaul longHaul) {
		this.longHaul = longHaul;
	}
	public LongHaul getShortHaul() {
		return shortHaul;
	}
	public void setShortHaul(LongHaul shortHaul) {
		this.shortHaul = shortHaul;
	}
	public LongHaul getAsnData() {
		return asnData;
	}
	public void setAsnData(LongHaul asnData) {
		this.asnData = asnData;
	}
	public LongHaul getDeliveryData() {
		return deliveryData;
	}
	public void setDeliveryData(LongHaul deliveryData) {
		this.deliveryData = deliveryData;
	}
	public ArrayList<OverFlowData> getOverflowData() {
		return overflowData;
	}
	public void setOverflowData(ArrayList<OverFlowData> overflowData) {
		this.overflowData = overflowData;
	}
	
	
}


