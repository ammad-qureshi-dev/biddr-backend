/* (C) 2026 
bidder.app */
package com.bidder.service.models.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record BidRequest(@NotNull UUID auctionId,

		@NotNull UUID itemId,

		@NotNull @DecimalMin("0.0") BigDecimal amount,

		@NotNull LocalDateTime placedAt,

		Boolean accepted, Boolean rejected, Boolean active,

		LocalDateTime expiresAt,

		String rejectReason

) {
}
