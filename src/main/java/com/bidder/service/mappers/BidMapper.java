/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

import java.time.LocalDateTime;

import com.bidder.service.models.Bid;
import com.bidder.service.models.request.BidRequest;
import com.bidder.service.models.response.BidDto;

public class BidMapper {

	public static Bid requestToEntity(BidRequest request, LocalDateTime auctionExpiryDate) {
		return Bid.builder().accepted(false).active(true).amount(request.amount()).placedAt(LocalDateTime.now())
				.rejected(false).expiresAt(request.expiresAt() == null ? auctionExpiryDate : request.expiresAt())
				.build();
	}

	public static BidDto entityToRequest(Bid entity) {

		if (entity == null) {
			return null;
		}

		return new BidDto(entity.getId(), entity.getItem().getAuction().getId(), entity.getAmount(),
				entity.isAccepted(), entity.isRejected(), entity.getRejectReason(), entity.isActive(),
				entity.getPlacedAt(), entity.getExpiresAt(), AppUserMapper.entityToRequest(entity.getBidder()));
	}
}
