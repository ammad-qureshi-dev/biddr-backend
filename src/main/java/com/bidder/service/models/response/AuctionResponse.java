/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AuctionResponse(UUID id, List<ItemResponse> items, LocalDateTime startTime, LocalDateTime endTime) {

}
