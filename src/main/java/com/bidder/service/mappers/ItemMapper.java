/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

import com.bidder.service.models.Item;
import com.bidder.service.models.request.BiddingItemRequest;
import com.bidder.service.models.response.ItemResponse;

public class ItemMapper {

	public static Item requestToEntity(BiddingItemRequest request) {
		return Item.builder().title(request.title()).description(request.description())
				.minimumPrice(request.minimumPrice()).build();
	}

	public static ItemResponse entityToResponse(Item entity) {

		if (entity == null) {
			return null;
		}

		return new ItemResponse(entity.getAuction().getId(), entity.getId(), entity.getTitle(), entity.getDescription(),
				entity.getMinimumPrice(), entity.getPriceSoldAt(), BidMapper.entityToRequest(entity.getHighestBid()),
				entity.getBids().stream().map(BidMapper::entityToRequest).toList(),
				BidMapper.entityToRequest(entity.getAcceptedBid()));
	}
}
