/* (C) 2026 
bidder.app */
package com.bidder.service.models.response.summary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.bidder.service.models.AuctionCategory;
import com.bidder.service.models.AuctionStatus;

public record AuctionSummaryResponse(UUID id, String title, AuctionStatus status, List<AuctionCategory> categories,
		int itemCount, LocalDateTime startTime, LocalDateTime endTime) {
}
