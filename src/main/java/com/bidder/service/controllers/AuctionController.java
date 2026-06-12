/* (C) 2026 
bidder.app */
package com.bidder.service.controllers;

import java.util.UUID;

import com.bidder.service.models.request.AuctionRequest;
import com.bidder.service.models.response.ApiResponse;
import com.bidder.service.models.response.AuctionResponse;
import com.bidder.service.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bidder.service.utils.Constants.Controller.BASE_URI;
import static com.bidder.service.utils.Constants.Controller.V1;

@RestController
@RequestMapping(BASE_URI + V1 + "/auction")
@RequiredArgsConstructor
public class AuctionController {

	private final AuctionService auctionService;

	@PostMapping
	public ResponseEntity<ApiResponse<UUID>> createAuction(@RequestBody AuctionRequest request) {
		var auctionId = auctionService.createAuction(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<UUID>builder().data(auctionId).build());
	}

	@PutMapping("/{auctionId}")
	public ResponseEntity<ApiResponse<UUID>> updateAuction(@PathVariable UUID auctionId,
			@RequestBody AuctionRequest request) {
		var response = auctionService.updateAuction(auctionId, request);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<UUID>builder().data(response).build());
	}

	@GetMapping("/{auctionId}")
	public ResponseEntity<ApiResponse<AuctionResponse>> getAuction(@PathVariable UUID auctionId) {
		var response = auctionService.getAuctionResponse(auctionId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<AuctionResponse>builder().data(response).build());
	}

}
