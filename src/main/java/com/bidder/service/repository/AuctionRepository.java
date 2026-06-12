/* (C) 2026 
bidder.app */
package com.bidder.service.repository;

import java.util.List;
import java.util.UUID;

import com.bidder.service.models.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {

	@Query("""
			select a
			from Auction a
			where a.auctionStatus != 'CLOSED'
			and a.endTime <= CURRENT_TIMESTAMP
			""")
	List<Auction> findOpenAndPausedAuctions();

}
