/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.bidder.service.models.response.summary.BidSummaryResponse;

public record ItemResponse(UUID id, String title, String description, BigDecimal minimumPrice,
		BidSummaryResponse highestBid, List<BidSummaryResponse> bids, BigDecimal priceSoldAt) {
}
