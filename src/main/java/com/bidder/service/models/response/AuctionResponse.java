/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.bidder.service.models.AuctionStatus;

public record AuctionResponse(UUID id, UUID ownerId, String title, AuctionStatus auctionStatus,
		List<ItemResponse> items, LocalDateTime startTime, LocalDateTime endTime) {

}
