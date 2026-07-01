/* (C) 2026 
bidder.app */
package com.bidder.service.schedulers;

import java.time.LocalDateTime;

import com.bidder.service.service.BidService;
import com.bidder.service.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.bidder.service.utils.Constants.Messages.REJECT_REASON_BID_EXPIRED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemScheduler {

	private final ItemService itemService;
	private final BidService bidService;

	@Transactional
	@Scheduled(fixedRate = 60000)
	public void recomputeHighestBidsAfterBidExpires() {
		log.info("ItemScheduler: starting recomputeHighestBidsAfterBidExpires job...");

		var itemsWithExpiredBids = itemService.findItemsWithExpiredBids();

		if (itemsWithExpiredBids.isEmpty()) {
			log.info("No items found with expired bids @ {}", LocalDateTime.now());
			return;
		}

		itemsWithExpiredBids.forEach(i -> {
			var prevHighestBid = i.getHighestBid();

			if (prevHighestBid != null) {
				bidService.rejectBid(prevHighestBid.getId(), REJECT_REASON_BID_EXPIRED);
			}

			var nextHighestUnexpiredBid = bidService.findNextHighestUnexpiredBidForItem(i.getId());
			nextHighestUnexpiredBid.ifPresent(i::setHighestBid);
		});

		log.info("ItemScheduler: recomputeHighestBidsAfterBidExpires job completed");
	}
}
