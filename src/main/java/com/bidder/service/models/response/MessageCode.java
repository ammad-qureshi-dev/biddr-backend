/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

public enum MessageCode {

	SERVICE_RESPONSE_CHECK("This service is sunning"), INCORRECT_CREDENTIALS("Incorrect credentials provided");

	private final String message;

	MessageCode(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}
