/* (C) 2026 
bidder.app */
package com.bidder.service.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.bidder.service.mappers.ItemMapper;
import com.bidder.service.models.Auction;
import com.bidder.service.models.Item;
import com.bidder.service.models.request.BiddingItemRequest;
import com.bidder.service.models.response.ItemResponse;
import com.bidder.service.repository.AuctionRepository;
import com.bidder.service.repository.BidRepository;
import com.bidder.service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final AuctionRepository auctionRepository;
	private final BidRepository bidRepository;

	public void createItems(List<BiddingItemRequest> itemsRequest, Auction auction) {

		final var items = itemsRequest.stream().map(ItemMapper::requestToEntity).toList();

		items.forEach(e -> e.setAuction(auction));
		itemRepository.saveAll(items);
	}

	public ItemResponse getItem(UUID id) {
		var item = getItemById(id);
		return ItemMapper.entityToResponse(item);
	}

	public Item getItemById(UUID id) {
		return itemRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Item not found"));
	}

	public List<Item> findItemsWithExpiredBids() {
		return bidRepository.findItemsWithExpiredBids();
	}
}
