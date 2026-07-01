/* (C) 2026 
bidder.app */
package com.bidder.service.controllers;

import java.util.List;
import java.util.UUID;

import com.bidder.service.models.response.ApiResponse;
import com.bidder.service.models.response.ItemResponse;
import com.bidder.service.models.response.summary.BidSummaryResponse;
import com.bidder.service.service.BidService;
import com.bidder.service.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bidder.service.utils.Constants.Controller.BASE_URI;
import static com.bidder.service.utils.Constants.Controller.V1;

@RestController
@RequestMapping(BASE_URI + V1 + "/item")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;
	private final BidService bidService;

	@GetMapping("/{itemId}")
	public ResponseEntity<ApiResponse<ItemResponse>> getItem(@PathVariable UUID itemId) {
		var item = itemService.getItem(itemId);
		return ResponseEntity.ok().body(ApiResponse.<ItemResponse>builder().data(item).build());
	}

	@GetMapping("/{itemId}/bids")
	public ResponseEntity<ApiResponse<List<BidSummaryResponse>>> getBidsForItem(@PathVariable UUID itemId) {
		var bids = bidService.getBidsForItem(itemId);
		return ResponseEntity.ok().body(ApiResponse.<List<BidSummaryResponse>>builder().data(bids).build());
	}
}
