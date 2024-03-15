package com.api.dms.auth.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ResponseModel {
	
	public ResponseModel(RequestModel requestModel) {
		this.setRequestId(requestModel.getRequestId());
		
		this.setRequestDate(requestModel.getRequestDate());
		
		this.setResponseId(this.getRequestId() + "-" + this.generateString(10));
		
		this.setResponseDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
	}
	
	public String generateString(int length) {
		String characters = "abcdefghijklmnopqrstuvwxyz1234567890";
		Random rnd = new Random();
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rnd.nextInt(characters.length()));
		}
		return new String(text);
	}
	
	private String requestId;
	
	private String requestDate;

	private String responseId;
	
	private String responseDate;
	
	private String status;
	
	private String error;
	
	private String message;
	
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public String getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(String responseDate) {
		this.responseDate = responseDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
