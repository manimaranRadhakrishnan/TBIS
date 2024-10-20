package com.tbis.api.master.model;

public class AsnIncidentLog {
	private long asnIncidentLogId;
	private long asnId;
	private int incidentId;
	private String scwComments;
	private String document;
	private String documentName;
	private String documentPath;
	private int userId;
	
	public long getAsnIncidentLogId() {
		return asnIncidentLogId;
	}
	public void setAsnIncidentLogId(long asnIncidentLogId) {
		this.asnIncidentLogId = asnIncidentLogId;
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
	public String getScwComments() {
		return scwComments;
	}
	public void setScwComments(String scwComments) {
		this.scwComments = scwComments;
	}
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	
	
}
