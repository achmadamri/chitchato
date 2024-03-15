package com.api.dms.auth.model.auth;

import com.api.dms.auth.model.ResponseModel;

import io.jsonwebtoken.Claims;

public class PostCheckResponseModel extends ResponseModel {
	
	public PostCheckResponseModel(PostCheckRequestModel requestModel) {
		super(requestModel);
	}
	
	private Claims claims;

	public Claims getClaims() {
		return claims;
	}

	public void setClaims(Claims claims) {
		this.claims = claims;
	}
}
