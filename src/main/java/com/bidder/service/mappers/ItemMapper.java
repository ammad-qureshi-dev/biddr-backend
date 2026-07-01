/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

import com.bidder.service.models.Item;
import com.bidder.service.models.request.BiddingItemRequest;
import com.bidder.service.models.response.ItemResponse;
import com.bidder.service.models.response.summary.BidSummaryResponse;
import com.bidder.service.models.response.summary.ItemSummaryResponse;

public class ItemMapper {

	public static Item requestToEntity(BiddingItemRequest request) {
		return Item.builder().title(request.title()).description(request.description())
				.minimumPrice(request.minimumPrice()).build();
	}

	// @Deprecated(forRemoval = true)
	public static ItemResponse entityToResponse(Item entity) {

		if (entity == null) {
			return null;
		}

		BidSummaryResponse highestBid = BidMapper.entityToSummary(entity.getHighestBid());

		return new ItemResponse(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getMinimumPrice(),
				highestBid, entity.getBids().size(), entity.getPriceSoldAt());
	}

	public static ItemSummaryResponse entityToSummary(Item i) {
		if (i == null) {
			return null;
		}

		var status = i.getPriceSoldAt() != null;
		return new ItemSummaryResponse(i.getId(), i.getAuction().getId(), i.getTitle(), i.getDescription(),
				i.getMinimumPrice(), status, i.getBids().size());
	}
}
