/* (C) 2026 
bidder.app */
package com.bidder.service.controllers;

import java.util.UUID;

import com.bidder.service.models.AppUserPrincipal;
import com.bidder.service.models.request.BidRejectRequest;
import com.bidder.service.models.request.BidRequest;
import com.bidder.service.models.response.ApiResponse;
import com.bidder.service.models.response.BidDto;
import com.bidder.service.models.response.BidResponse;
import com.bidder.service.service.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.bidder.service.utils.Constants.Controller.BASE_URI;
import static com.bidder.service.utils.Constants.Controller.V1;

@Slf4j
@RestController
@RequestMapping(BASE_URI + V1 + "/bid")
@RequiredArgsConstructor
public class BidController {

	private final BidService bidService;

	@PostMapping
	public ResponseEntity<ApiResponse<BidResponse>> createBid(@AuthenticationPrincipal AppUserPrincipal bidder,
			@RequestBody BidRequest request) {
		var response = bidService.createBid(request, bidder);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.<BidResponse>builder().data(response).build());
	}

	@PutMapping("/{bidId}")
	public ResponseEntity<ApiResponse<BidResponse>> updateBid(@PathVariable UUID bidId, @RequestBody BidRequest request,
			@AuthenticationPrincipal AppUserPrincipal bidder) {
		if (!bidService.isBidOwner(bidId, bidder.getUserId())) {
			log.error("Cannot update bid - userId={} does not own bid={}", bidder.getUserId(), bidId);
			throw new IllegalStateException("Cannot update bid - user does not own bid");
		}

		var response = bidService.updateBid(bidId, request, bidder);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<BidResponse>builder().data(response).build());
	}

	@PostMapping("/accept/{bidId}")
	public ResponseEntity<ApiResponse<Boolean>> acceptBid(@PathVariable UUID bidId,
			@AuthenticationPrincipal AppUserPrincipal bidder) {

		if (!bidService.isBidOwner(bidId, bidder.getUserId())) {
			log.error("Cannot accept bid - userId={} does not own bid={}", bidder.getUserId(), bidId);
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Boolean>builder().data(false).build());
		}

		bidService.acceptBid(bidId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Boolean>builder().data(true).build());
	}

	@PostMapping("/reject/{bidId}")
	public ResponseEntity<ApiResponse<Boolean>> rejectBid(@PathVariable UUID bidId,
			@RequestBody BidRejectRequest request, @AuthenticationPrincipal AppUserPrincipal bidder) {

		if (!bidService.isBidOwner(bidId, bidder.getUserId())) {
			log.error("Cannot reject bid - userId={} does not own bid={}", bidder.getUserId(), bidId);
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Boolean>builder().data(false).build());
		}

		bidService.rejectBid(bidId, request.rejectReason());
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Boolean>builder().data(true).build());
	}

	@GetMapping("/{bidId}")
	public ResponseEntity<ApiResponse<BidDto>> getBidById(@PathVariable UUID bidId) {
		var bid = bidService.getBid(bidId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<BidDto>builder().data(bid).build());
	}
}
