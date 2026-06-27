/* (C) 2026 
bidder.app */
package com.bidder.service.models.request;

import com.bidder.service.models.ContactMethod;

public record TokenRequest(ContactMethod contactMethod, String email, String phoneNumber, String password) {

	@Override
	public String toString() {
		return """
				{
				    resetMethod: %s,
				    email: %s,
				    phoneNumber: %s,
				    password: ***
				}
				""".formatted(contactMethod, email, phoneNumber);
	}
}
