/* (C) 2026 
bidder.app */
package com.bidder.service.models.response.summary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bidder.service.models.BidStatus;

public record BidSummaryResponse(UUID id, UUID itemId, String itemName, BigDecimal amount, BidStatus status,
		String statusDescription, LocalDateTime placedAt, LocalDateTime expiresAt) {
}
