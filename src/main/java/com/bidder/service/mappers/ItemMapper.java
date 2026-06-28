/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

import java.util.Collections;
import java.util.List;

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

		List<BidSummaryResponse> bids = Collections.emptyList();
		if (!entity.getBids().isEmpty()) {
			bids = entity.getBids().stream().map(BidMapper::entityToSummary).toList();
		}

		BidSummaryResponse highestBid = BidMapper.entityToSummary(entity.getHighestBid());

		return new ItemResponse(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getMinimumPrice(),
				highestBid, bids, entity.getPriceSoldAt());
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
