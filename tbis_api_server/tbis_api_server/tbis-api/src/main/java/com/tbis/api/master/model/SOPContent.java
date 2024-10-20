package com.tbis.api.master.model;

public class SOPContent {
	private long tocId;
	private long sopId;
	private int sopTypeid;
	private String sopIcon;
	private String sopTitle;
	private String sopTopicTitle;
	private int sopTopicTypeid;
	private int sopParentid;
	private int sopTopicorder;
	private String sopSourceurl;
	public long getTocId() {
		return tocId;
	}
	public void setTocId(long tocId) {
		this.tocId = tocId;
	}
	public long getSopId() {
		return sopId;
	}
	public void setSopId(long sopId) {
		this.sopId = sopId;
	}
	public int getSopTypeid() {
		return sopTypeid;
	}
	public void setSopTypeid(int sopTypeid) {
		this.sopTypeid = sopTypeid;
	}
	public String getSopIcon() {
		return sopIcon;
	}
	public void setSopIcon(String sopIcon) {
		this.sopIcon = sopIcon;
	}
	public String getSopTitle() {
		return sopTitle;
	}
	public void setSopTitle(String sopTitle) {
		this.sopTitle = sopTitle;
	}
	public String getSopTopicTitle() {
		return sopTopicTitle;
	}
	public void setSopTopicTitle(String sopTopicTitle) {
		this.sopTopicTitle = sopTopicTitle;
	}
	public int getSopTopicTypeid() {
		return sopTopicTypeid;
	}
	public void setSopTopicTypeid(int sopTopicTypeid) {
		this.sopTopicTypeid = sopTopicTypeid;
	}
	public int getSopParentid() {
		return sopParentid;
	}
	public void setSopParentid(int sopParentid) {
		this.sopParentid = sopParentid;
	}
	public int getSopTopicorder() {
		return sopTopicorder;
	}
	public void setSopTopicorder(int sopTopicorder) {
		this.sopTopicorder = sopTopicorder;
	}
	public String getSopSourceurl() {
		return sopSourceurl;
	}
	public void setSopSourceurl(String sopSourceurl) {
		this.sopSourceurl = sopSourceurl;
	}

}
