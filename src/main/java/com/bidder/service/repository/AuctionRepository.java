/* (C) 2026 
bidder.app */
package com.bidder.service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.bidder.service.models.Auction;
import com.bidder.service.models.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {

	@Query("""
			select a
			from Auction a
			where a.auctionStatus != 'CLOSED'
			and a.endTime <= CURRENT_TIMESTAMP
			""")
	List<Auction> findOpenAndPausedAuctions();

	@Query("""
			select a
			from Auction a
			where (:title is null or lower(a.title) like lower(concat('%', :title, '%')))
			and (:status is null or a.auctionStatus = :status)
			and (:startAfter is null or a.startTime >= :startAfter)
			and (:endBefore is null or a.endTime <= :endBefore)
			""")
	List<Auction> search(@Param("title") String title, @Param("status") AuctionStatus status,
			@Param("startAfter") LocalDateTime startAfter, @Param("endBefore") LocalDateTime endBefore);

}
