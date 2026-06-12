/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ItemResponse(UUID auctionId, UUID id, String title, String description, BigDecimal minimumPrice,
		BigDecimal priceSoldAt,

		BidDto highestBid, List<BidDto> bids, BidDto acceptedBid) {
}
