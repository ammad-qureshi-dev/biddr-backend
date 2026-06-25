/* (C) 2026 
bidder.app */
package com.bidder.service.models.request;

import com.bidder.service.models.ContactMethod;

public record ResetPasswordRequest(ContactMethod resetMethod, String email, String phoneNumber, String password) {

	@Override
	public String toString() {
		return """
				{
				    resetMethod: %s,
				    email: %s,
				    phoneNumber: %s,
				    password: ***
				}
				""".formatted(resetMethod, email, phoneNumber);
	}
}
