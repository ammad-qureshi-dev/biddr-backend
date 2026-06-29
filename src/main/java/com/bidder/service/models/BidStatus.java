/* (C) 2026 
bidder.app */
package com.bidder.service.models;

public enum BidStatus {
	WINNER, // Winner of the item
	REJECTED, // Rejected due to insufficient funds, cannot pay properly, etc.
	ACTIVE, // Current highest bid
	OUTBID, WITHDRAWN // Bidder can opt to withdraw bid
}
