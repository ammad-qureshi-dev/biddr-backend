/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

import com.bidder.service.models.Auction;
import com.bidder.service.models.request.AuctionRequest;
import com.bidder.service.models.response.AuctionResponse;
import com.bidder.service.models.response.summary.AuctionSummaryResponse;

public class AuctionMapper {

	public static Auction requestToEntity(AuctionRequest request) {
		return Auction.builder().title(request.title()).startTime(request.startTime()).endTime(request.endTime())
				.items(request.biddingItems().stream().map(ItemMapper::requestToEntity).toList())
				.categories(request.categories()).build();
	}

	// @Deprecated(forRemoval = true)
	public static AuctionResponse entityToResponse(Auction entity) {
		var itemsDto = entity.getItems().stream().map(ItemMapper::entityToResponse).toList();
		return new AuctionResponse(entity.getId(), entity.getOwner().getId(), entity.getTitle(),
				entity.getAuctionStatus(), entity.getCategories(), itemsDto, entity.getStartTime(),
				entity.getEndTime());
	}

	public static AuctionSummaryResponse entityToSummary(Auction a) {
		return new AuctionSummaryResponse(a.getId(), a.getTitle(), a.getAuctionStatus(), a.getCategories(),
				a.getItems().size(), a.getStartTime(), a.getEndTime());
	}

}
