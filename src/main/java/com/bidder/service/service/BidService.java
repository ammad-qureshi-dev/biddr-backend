/* (C) 2026 
bidder.app */
package com.bidder.service.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import com.bidder.service.mappers.BidMapper;
import com.bidder.service.models.*;
import com.bidder.service.models.request.BidRequest;
import com.bidder.service.models.request.NotificationRequest;
import com.bidder.service.models.response.BidDto;
import com.bidder.service.models.response.BidResponse;
import com.bidder.service.repository.BidRepository;
import com.bidder.service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bidder.service.service.NotificationService.ALL_NOTIFICATION_METHODS;
import static com.bidder.service.utils.Constants.Messages.*;

@Service
@RequiredArgsConstructor
public class BidService {

	private final AuctionService auctionService;
	private final AppUserService appUserService;
	private final BidRepository bidRepository;
	private final ItemService itemService;
	private final ItemRepository itemRepository;
	private final NotificationService notificationService;

	@Transactional
	public BidResponse createBid(BidRequest request, AppUserPrincipal bidder) {
		var auction = auctionService.getAuctionById(request.auctionId());
		isAuctionOpen(auction);

		var item = itemService.getItemById(request.itemId());

		validateBid(request, auction);

		var bid = BidMapper.requestToEntity(request, auction.getEndTime());
		bid.setItem(item);

		setBidder(bidder.getUserId(), bid);

		bidRepository.save(bid);

		notificationService.sendNotification(new NotificationRequest(bidder.getUserId(), BID_REQUEST_SENT,
				String.format(BID_REQUEST_MESSAGE, item.getTitle(), auction.getTitle()), NotificationType.SUCCESS,
				ALL_NOTIFICATION_METHODS));

		return new BidResponse(isHighestBid(bid), bid.getId());
	}

	@Transactional
	public BidResponse updateBid(UUID bidId, BidRequest request, AppUserPrincipal bidder) {
		var auction = auctionService.getAuctionById(request.auctionId());
		isAuctionOpen(auction);

		validateBid(request, auction);

		var bid = getBidById(bidId);

		if (!isOriginalBidder(bid, bidder.getUserId())) {
			throw new IllegalStateException("The original bidder can only place this bid");
		}

		bid.setAmount(request.amount() == null ? bid.getAmount() : request.amount());
		bid.setAccepted(false);
		bid.setRejected(false);
		bid.setActive(true);
		bid.setExpiresAt(request.expiresAt() == null ? auction.getEndTime() : request.expiresAt());

		bidRepository.save(bid);

		notificationService.sendNotification(new NotificationRequest(
				bidder.getUserId(), BID_REQUEST_UPDATED, String.format(BID_REQUEST_UPDATED_MESSAGE,
						bid.getItem().getTitle(), bid.getUpdatedAt(), bid.getAmount()),
				NotificationType.SUCCESS, ALL_NOTIFICATION_METHODS));

		return new BidResponse(isHighestBid(bid), bid.getId());
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

		bid.setActive(false);
		bid.setRejected(true);
		bid.setRejectReason(rejectReason);
		bidRepository.save(bid);

		notificationService.sendNotification(new NotificationRequest(bid.getBidder().getId(), BID_REQUEST_REJECTED,
				String.format(BID_REQUEST_REJECTED_MESSAGE, bid.getItem().getTitle()), NotificationType.INFO,
				ALL_NOTIFICATION_METHODS));

		// If the bid that got rejected was the highest bid, replace it with new highest
		final var item = bid.getItem();
		if (item.getHighestBid().getId().equals(bid.getId())) {
			item.setHighestBid(findNextHighestUnexpiredBidForItem(item.getId()));
			itemRepository.save(item);
		}
	}

	public Bid findNextHighestUnexpiredBidForItem(UUID itemId) {
		var activeBids = bidRepository.findActiveBids(itemId);

		if (activeBids == null || activeBids.isEmpty()) {
			return null;
		}

		return activeBids.stream().max(Comparator.comparing(Bid::getAmount)).orElseThrow();
	}

	public void acceptBid(UUID bidId) {
		final var acceptedBid = getBidById(bidId);

		final var auction = acceptedBid.getItem().getAuction();
		isAuctionOpen(auction);

		final var item = itemService.getItemById(acceptedBid.getItem().getId());

		if (item.getAcceptedBid() != null) {
			throw new IllegalStateException("Cannot accept anymore bids, a bid for this item has been selected");
		}

		if (!acceptedBid.isActive()) {
			throw new IllegalStateException("This bid cannot be accepted, it is in-active");
		} else if (acceptedBid.isRejected()) {
			throw new IllegalStateException("This bid cannot be accepted, it has been rejected");
		}

		acceptedBid.setAccepted(true);
		acceptedBid.setActive(false);
		acceptedBid.setRejected(false);

		bidRepository.save(acceptedBid);

		item.setAcceptedBid(acceptedBid);

		itemRepository.save(item);

		notificationService.sendNotification(new NotificationRequest(acceptedBid.getBidder().getId(),
				BID_REQUEST_ACCEPTED, String.format(BID_REQUEST_ACCEPTED_MESSAGE, acceptedBid.getItem().getTitle()),
				NotificationType.INFO, ALL_NOTIFICATION_METHODS));
	}

	public boolean isBidOwner(UUID bidId, UUID userId) {
		final var bid = getBidById(bidId);
		final var user = appUserService.getAppUserById(userId);
		return bid.getBidder().getId().equals(user.getId());
	}

	public BidDto getBid(UUID bidId) {
		return BidMapper.entityToRequest(getBidById(bidId));
	}

	private boolean isHighestBid(Bid newBid) {
		var item = itemService.getItemById(newBid.getItem().getId());
		var current = item.getHighestBid();

		boolean meetsMinimum = newBid.getAmount().compareTo(item.getMinimumPrice()) >= 0;

		boolean isHighest = meetsMinimum && (current == null || newBid.getAmount().compareTo(current.getAmount()) > 0
				|| (newBid.getAmount().compareTo(current.getAmount()) == 0
						&& newBid.getPlacedAt().isBefore(current.getPlacedAt())));

		if (isHighest) {
			item.setHighestBid(newBid);
		}

		return isHighest;
	}

	private boolean isOriginalBidder(Bid bid, UUID bidderId) {
		return bid.getBidder().getId().equals(bidderId);
	}

	private void validateBid(BidRequest request, Auction auction) {
		var placedAt = request.placedAt();

		if (placedAt.isBefore(auction.getStartTime()) || placedAt.isAfter(auction.getEndTime())) {
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
