package com.api.dms.auth.model.auth;

import com.api.dms.auth.model.RequestModel;

public class PutUpdateRequestModel extends RequestModel {
	private String tbaEmail;

	private String tbaPassword;

	private String newPassword;

	private String tbaStatus;
	
	private String tbaRole;

	public String getTbaEmail() {
		return tbaEmail;
	}

	public void setTbaEmail(String tbaEmail) {
		this.tbaEmail = tbaEmail;
	}

	public String getTbaPassword() {
		return tbaPassword;
	}

	public void setTbaPassword(String tbaPassword) {
		this.tbaPassword = tbaPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getTbaStatus() {
		return tbaStatus;
	}

	public void setTbaStatus(String tbaStatus) {
		this.tbaStatus = tbaStatus;
	}

	public String getTbaRole() {
		return tbaRole;
	}

	public void setTbaRole(String tbaRole) {
		this.tbaRole = tbaRole;
	}
}
