/* (C) 2026 
bidder.app */
package com.bidder.service.models.request;

import jakarta.validation.constraints.NotNull;
import models.ContactType;

public record PreferredContactMethodRequest(@NotNull ContactType contactType,

		// Allow user to update their contact method if needed while also allowing them
		// to update their preferred contact method
		String contact) {
}
