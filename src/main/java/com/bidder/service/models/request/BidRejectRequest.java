/* (C) 2026 
bidder.app */
package com.bidder.service.models.request;

import jakarta.validation.constraints.NotNull;

public record BidRejectRequest(

		@NotNull String rejectReason) {
}
