package com.api.dms.auth.model.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.api.dms.auth.model.RequestModel;

public class PostCheckRequestModel extends RequestModel {
	@NotEmpty(message = "Email may not be empty")
	@Email(message = "Email should be valid")
	@Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters long")
	private String tbaEmail;

	@NotEmpty(message = "Token may not be empty")
	@Size(min = 1, max = 1000, message = "Name must be between 1 and 1000 characters long")
	private String tbaToken;

	public String getTbaEmail() {
		return tbaEmail;
	}

	public void setTbaEmail(String tbaEmail) {
		this.tbaEmail = tbaEmail;
	}

	public String getTbaToken() {
		return tbaToken;
	}

	public void setTbaToken(String tbaToken) {
		this.tbaToken = tbaToken;
	}
}
