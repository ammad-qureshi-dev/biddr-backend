/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

import com.bidder.service.models.Auction;
import com.bidder.service.models.request.AuctionRequest;
import com.bidder.service.models.response.AuctionResponse;

public class AuctionMapper {

	public static Auction requestToEntity(AuctionRequest request) {
		return Auction.builder().title(request.title()).startTime(request.startTime()).endTime(request.endTime())
				.items(request.biddingItems().stream().map(ItemMapper::requestToEntity).toList()).build();
	}

	public static AuctionResponse entityToResponse(Auction entity) {
		var itemsDto = entity.getItems().stream().map(ItemMapper::entityToResponse).toList();
		return new AuctionResponse(entity.getId(), itemsDto, entity.getStartTime(), entity.getEndTime());
	}
}
