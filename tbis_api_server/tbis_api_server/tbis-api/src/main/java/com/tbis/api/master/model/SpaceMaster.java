package com.tbis.api.master.model;

import java.util.ArrayList;

public class SpaceMaster {
	 private long spaceId;
	 private int subLocationId;
	 private String spaceName;
	 private String colorCode;
	 private int lineNo;
	 private int columnNo;
	 private int lineUsageId;
	 private int lineSpaceId;
	 private long customerid;
	 private long partId ;
	 private int userId;
	 private int startCell;
	 private int endCell;
	 private int startRow;
	 private int endRow;
	 private int fifoOrder;
	 private int maxBins;
	 private int fromLineSpaceId;
	 private int toLineSpaceId;
	 private String partSpaceName;
	 private int spaceOccupation;
	 private String customerName;
	 private String lineUsageName;
	 private String partName;
	 private ArrayList<PartMaster> parts;
	 
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	public int getSubLocationId() {
		return subLocationId;
	}
	public void setSubLocationId(int subLocationId) {
		this.subLocationId = subLocationId;
	}
	public String getSpaceName() {
		return spaceName;
	}
	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	public int getLineNo() {
		return lineNo;
	}
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	public int getColumnNo() {
		return columnNo;
	}
	public void setColumnNo(int columnNo) {
		this.columnNo = columnNo;
	}
	public int getLineUsageId() {
		return lineUsageId;
	}
	public void setLineUsageId(int lineUsageId) {
		this.lineUsageId = lineUsageId;
	}
	
	public int getLineSpaceId() {
		return lineSpaceId;
	}
	public void setLineSpaceId(int lineSpaceId) {
		this.lineSpaceId = lineSpaceId;
	}
	public long getCustomerid() {
		return customerid;
	}
	public void setCustomerid(long customerid) {
		this.customerid = customerid;
	}
	public long getPartId() {
		return partId;
	}
	public void setPartId(long partId) {
		this.partId = partId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getStartCell() {
		return startCell;
	}
	public void setStartCell(int startCell) {
		this.startCell = startCell;
	}
	public int getEndCell() {
		return endCell;
	}
	public void setEndCell(int endCell) {
		this.endCell = endCell;
	}
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	public int getFifoOrder() {
		return fifoOrder;
	}
	public void setFifoOrder(int fifoOrder) {
		this.fifoOrder = fifoOrder;
	}
	public int getMaxBins() {
		return maxBins;
	}
	public void setMaxBins(int maxBins) {
		this.maxBins = maxBins;
	}
	public int getFromLineSpaceId() {
		return fromLineSpaceId;
	}
	public void setFromLineSpaceId(int fromLineSpaceId) {
		this.fromLineSpaceId = fromLineSpaceId;
	}
	public int getToLineSpaceId() {
		return toLineSpaceId;
	}
	public void setToLineSpaceId(int toLineSpaceId) {
		this.toLineSpaceId = toLineSpaceId;
	}
	public String getPartSpaceName() {
		return partSpaceName;
	}
	public void setPartSpaceName(String partSpaceName) {
		this.partSpaceName = partSpaceName;
	}
	public ArrayList<PartMaster> getParts() {
		return parts;
	}
	public void setParts(ArrayList<PartMaster> parts) {
		this.parts = parts;
	}
	public int getSpaceOccupation() {
		return spaceOccupation;
	}
	public void setSpaceOccupation(int spaceOccupation) {
		this.spaceOccupation = spaceOccupation;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getLineUsageName() {
		return lineUsageName;
	}
	public void setLineUsageName(String lineUsageName) {
		this.lineUsageName = lineUsageName;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	
	
}
