package com.tbis.api.master.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DashboardDetail implements Serializable{
	private ArrayList<VendorFillRate> vendorDetail;
	private ArrayList<VendorFillRate> partDetail;
	public ArrayList<VendorFillRate> getVendorDetail() {
		return vendorDetail;
	}
	public void setVendorDetail(ArrayList<VendorFillRate> vendorDetail) {
		this.vendorDetail = vendorDetail;
	}
	public ArrayList<VendorFillRate> getPartDetail() {
		return partDetail;
	}
	public void setPartDetail(ArrayList<VendorFillRate> partDetail) {
		this.partDetail = partDetail;
	}
	
}
