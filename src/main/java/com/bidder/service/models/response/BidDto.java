/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BidDto(UUID id, BigDecimal amount, boolean accepted, boolean rejected, String rejectedReason,
		boolean active, LocalDateTime placedAt, LocalDateTime expiresAt,

		AppUserDto bidder) {

}
