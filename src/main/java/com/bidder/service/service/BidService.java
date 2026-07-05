/* (C) 2026 
bidder.app */
package com.bidder.service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bidder.service.mappers.BidMapper;
import com.bidder.service.models.*;
import com.bidder.service.models.request.BidRequest;
import com.bidder.service.models.response.summary.BidSummaryResponse;
import com.bidder.service.repository.BidRepository;
import com.bidder.service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.bidder.service.utils.Constants.Messages.*;

@Service
@RequiredArgsConstructor
public class BidService {

	private final AuctionService auctionService;
	private final AppUserService appUserService;
	private final BidRepository bidRepository;
	private final ItemService itemService;
	private final ItemRepository itemRepository;

	@Transactional
	public UUID createBid(BidRequest request, AppUserPrincipal bidder) {
		var auction = auctionService.getAuctionById(request.auctionId());
		isAuctionOpen(auction);

		var item = itemService.getItemById(request.itemId());
		var bid = BidMapper.requestToEntity(request, auction.getEndTime());
		bid.setItem(item);

		validateBid(request, auction, bid, bidder.getUserId());

		// Note: we don't have to iterate through every bid and update to OUTBID since
		// we will always update the highest bid

		var previousHighestBid = item.getHighestBid();
		if (previousHighestBid != null) {
			previousHighestBid.setStatus(BidStatus.OUTBID);
			bidRepository.save(previousHighestBid);
		}

		item.setHighestBid(bid);
		itemRepository.save(item);

		setBidder(bidder.getUserId(), bid);
		bid.setStatus(BidStatus.ACTIVE);
		bidRepository.save(bid);

		// ToDo: kafka call for BID_REQUEST_SENT

		return bid.getId();
	}

	@Transactional
	public UUID updateBid(UUID bidId, BidRequest request, AppUserPrincipal bidder) {
		var auction = auctionService.getAuctionById(request.auctionId());
		isAuctionOpen(auction);

		var previousBid = getBidById(bidId);

		validateBid(request, auction, previousBid, bidder.getUserId());

		if (!isOriginalBidder(previousBid, bidder.getUserId())) {
			throw new IllegalStateException("The original bidder can only place this bid");
		}

		// De-activate old bid
		previousBid.setStatus(BidStatus.OUTBID);
		bidRepository.save(previousBid);

		// Create a new bid
		var item = previousBid.getItem();
		var newBid = BidMapper.requestToEntity(request, auction.getEndTime());
		newBid.setItem(item);

		item.setHighestBid(newBid);
		itemRepository.save(item);

		setBidder(bidder.getUserId(), newBid);
		newBid.setStatus(BidStatus.ACTIVE);
		bidRepository.save(newBid);

		// ToDo: kafka call for BID_REQUEST_UPDATED

		return newBid.getId();
	}

	public Bid getBidById(UUID id) {
		return bidRepository.findById(id).orElseThrow(() -> new IllegalStateException("Bid cannot be found"));
	}

	public void rejectBid(UUID bidId, String rejectReason) {
		final var bid = getBidById(bidId);

		final var auction = bid.getItem().getAuction();
		isAuctionOpen(auction);

		if (bid == null) {
			throw new IllegalStateException("Bid not found");
		}

		bid.setStatus(BidStatus.REJECTED);
		bid.setStatusDescription(rejectReason);
		bidRepository.save(bid);

		var message = StringUtils.hasLength(rejectReason)
				? rejectReason
				: String.format(BID_REQUEST_REJECTED_MESSAGE, bid.getItem().getTitle());

		// ToDo: kafka call for BID_REQUEST_SENT

		// If the bid that got rejected was the highest bid, replace it with new highest
		final var item = bid.getItem();
		if (item.getHighestBid().getId().equals(bid.getId())) {
			var nextBid = findNextHighestUnexpiredBidForItem(item.getId());

			if (nextBid.isEmpty()) {
				item.setHighestBid(null);
			} else {
				var b = nextBid.get();
				b.setStatus(BidStatus.ACTIVE);
				bidRepository.save(b);
				item.setHighestBid(b);
			}

			itemRepository.save(item);
		}
	}

	public Optional<Bid> findNextHighestUnexpiredBidForItem(UUID itemId) {
		var activeBids = bidRepository.findUnexpiredBids(itemId);

		if (activeBids == null || activeBids.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(activeBids.getFirst());
	}

	public void acceptBid(UUID bidId) {
		final var acceptedBid = getBidById(bidId);
		final var item = acceptedBid.getItem();
		final var auction = item.getAuction();
		isAuctionOpen(auction);

		if (item.getAcceptedBid() != null) {
			throw new IllegalStateException("Cannot accept anymore bids, a bid for this item has been selected");
		}

		if (!BidStatus.ACTIVE.equals(acceptedBid.getStatus()) || !item.getHighestBid().getId().equals(bidId)) {
			throw new IllegalStateException("This bid cannot be accepted, it is not the highest bid");
		}

		acceptedBid.setStatus(BidStatus.WINNER);
		bidRepository.save(acceptedBid);

		item.setAcceptedBid(acceptedBid);

		itemRepository.save(item);

		// ToDo: kafka call for BID_REQUEST_accepted
	}

	public boolean isBidOwner(UUID bidId, UUID userId) {
		final var bid = getBidById(bidId);
		final var user = appUserService.getAppUserById(userId);
		return bid.getBidder().getId().equals(user.getId());
	}

	public BidSummaryResponse getBid(UUID bidId) {
		return BidMapper.entityToSummary(getBidById(bidId));
	}

	public List<BidSummaryResponse> getMyBids(UUID appUserId) {
		var myBids = bidRepository.findBidsByBidderId(appUserId);
		return myBids.stream().map(BidMapper::entityToSummary).toList();
	}

	public List<BidSummaryResponse> getBidsForItem(UUID itemId) {
		var item = itemService.getItemById(itemId);
		return item.getBids().stream().map(BidMapper::entityToSummary).toList();
	}

	private boolean isHighestBid(Bid newBid) {
		var item = newBid.getItem();
		var current = item.getHighestBid();

		if (current != null && newBid.getAmount().equals(current.getAmount())) {
			return true;
		}

		boolean meetsMinimum = newBid.getAmount().compareTo(item.getMinimumPrice()) >= 0;

		return meetsMinimum && (current == null || newBid.getAmount().compareTo(current.getAmount()) > 0
				|| (newBid.getAmount().compareTo(current.getAmount()) == 0
						&& newBid.getPlacedAt().isBefore(current.getPlacedAt())));
	}

	private boolean isOriginalBidder(Bid bid, UUID bidderId) {
		return bid.getBidder().getId().equals(bidderId);
	}

	private void validateBid(BidRequest request, Auction auction, Bid newBid, UUID bidderId) {

		if (!isHighestBid(newBid)) {
			throw new IllegalStateException("Bid amount must be higher than the current highest bid");
		}

		if (bidderId.equals(auction.getOwner().getId())) {
			throw new IllegalStateException("Owner cannot place bids on their items");
		}

		var now = LocalDateTime.now();

		if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
			throw new IllegalStateException("Bid cannot be placed outside auction time window");
		}

		if (request.expiresAt() != null && (request.expiresAt().isAfter(auction.getEndTime())
				|| request.expiresAt().isBefore(LocalDateTime.now()))) {
			throw new IllegalStateException("Bid can only be between today's date and auction end date");
		}
	}

	private void setBidder(UUID bidderId, Bid bid) {
		var bidder = appUserService.getAppUserById(bidderId);
		bid.setBidder(bidder);
	}

	private void isAuctionOpen(Auction auction) {
		if (!AuctionStatus.OPEN.equals(auction.getAuctionStatus())) {
			throw new IllegalStateException(
					AuctionStatus.CLOSED.equals(auction.getAuctionStatus()) ? AUCTION_CLOSED : AUCTION_PAUSED);
		}
	}
}
