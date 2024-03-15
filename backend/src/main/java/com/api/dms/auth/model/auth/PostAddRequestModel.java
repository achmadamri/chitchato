package com.api.dms.auth.model.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.api.dms.auth.model.RequestModel;

public class PostAddRequestModel extends RequestModel {
	@NotEmpty(message = "Email may not be empty")
	@Email(message = "Email should be valid")
	@Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters long")
	private String tbaEmail;

	@NotEmpty(message = "Password may not be empty")
	@Size(min = 1, max = 32, message = "Password must be between 1 and 32 characters long")
	private String tbaPassword;

	@NotEmpty(message = "Status may not be empty")
	@Size(min = 1, max = 20, message = "Member Id must be between 1 and 20 characters long")
	private String tbaStatus;

	@NotEmpty(message = "Token salt may not be empty")
	@Size(min = 1, max = 255, message = "Token salt must be between 1 and 255 characters long")
	private String tbaTokenSalt;

	@NotEmpty(message = "Role may not be empty")
	@Size(min = 1, max = 255, message = "Role must be between 1 and 255 characters long")
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

	public String getTbaStatus() {
		return tbaStatus;
	}

	public void setTbaStatus(String tbaStatus) {
		this.tbaStatus = tbaStatus;
	}

	public String getTbaTokenSalt() {
		return tbaTokenSalt;
	}

	public void setTbaTokenSalt(String tbaTokenSalt) {
		this.tbaTokenSalt = tbaTokenSalt;
	}

	public String getTbaRole() {
		return tbaRole;
	}

	public void setTbaRole(String tbaRole) {
		this.tbaRole = tbaRole;
	}
}
