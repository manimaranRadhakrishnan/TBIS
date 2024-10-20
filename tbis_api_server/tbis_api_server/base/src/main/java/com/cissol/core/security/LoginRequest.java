package com.cissol.core.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

	public LoginRequest(String emailId, String password) {
		this.emailId = emailId;
		this.password = password;
	}

	@JsonProperty("emailId")
	private String emailId;

	@JsonProperty("password")
	private String password;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}