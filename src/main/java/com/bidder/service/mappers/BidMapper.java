/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

import java.time.LocalDateTime;

import com.bidder.service.models.Bid;
import com.bidder.service.models.request.BidRequest;
import com.bidder.service.models.response.summary.BidSummaryResponse;

public class BidMapper {

	public static Bid requestToEntity(BidRequest request, LocalDateTime auctionExpiryDate) {
		return Bid.builder().amount(request.amount()).placedAt(LocalDateTime.now())
				.expiresAt(request.expiresAt() == null ? auctionExpiryDate : request.expiresAt()).build();
	}

	public static BidSummaryResponse entityToSummary(Bid b) {
		if (b == null) {
			return null;
		}

		var item = b.getItem();

		return new BidSummaryResponse(b.getId(), item.getId(), item.getTitle(), b.getAmount(), b.getStatus(),
				b.getPlacedAt(), b.getExpiresAt());
	}
}
