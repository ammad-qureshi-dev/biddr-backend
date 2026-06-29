/* (C) 2026 
bidder.app */
package com.bidder.service.repository;

import java.util.List;
import java.util.UUID;

import com.bidder.service.models.Bid;
import com.bidder.service.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BidRepository extends JpaRepository<Bid, UUID> {

	@Query("""
			select distinct b.item
			from Bid b
			where b.expiresAt <= CURRENT_TIMESTAMP
			""")
	List<Item> findItemsWithExpiredBids();

	@Query("""
			select b
			from Bid b
			where b.item.id = :itemId
			and b.expiresAt > CURRENT_TIMESTAMP
			and b.status in (com.bidder.service.models.BidStatus.ACTIVE, com.bidder.service.models.BidStatus.OUTBID)
			order by b.amount desc
			""")
	List<Bid> findUnexpiredBids(@Param("itemId") UUID itemId);

	@Query("""
			select b
			from Bid b
			where b.bidder.id = :bidderId
			order by b.placedAt
			""")
	List<Bid> findBidsByBidderId(@Param("bidderId") UUID bidderId);
}
