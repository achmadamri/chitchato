package com.api.dms.auth.model.token;

import com.api.dms.auth.model.RequestModel;

public class TokenSyncRequestModel extends RequestModel {
	private String email;
	
	private String salt;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
}
