/* (C) 2026 
bidder.app */
package com.bidder.service.models.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record AuctionRequest(@NotNull String title,

		@NotNull LocalDateTime startTime,

		@NotNull LocalDateTime endTime,

		@Length(min = 1) List<BiddingItemRequest> biddingItems) {
}
