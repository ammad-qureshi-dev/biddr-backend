/* (C) 2026 
bidder.app */
package com.bidder.service.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Constants {

	@AllArgsConstructor
	public static class Controller {
		public static final String BASE_URI = "/api";
		public static final String V1 = "/v1";
	}

	public static class Database {
		public static final String SCHEMA = "bidder";
	}

	public static class ExceptionMessages {
		public static final String INVALID_CREDENTIALS = "Invalid credentials provided";
		public static final String INVALID_PASSWORD_FORMAT = "Invalid password format";
	}

	public static class Messages {
		public static final String REJECT_REASON_BID_EXPIRED = "Bid reached expiry date, bid is no longer acceptable for this item";
		public static final String AUCTION_CLOSED = "Cannot perform action -- this auction is closed";
		public static final String AUCTION_PAUSED = "Cannot perform action -- this auction is paused";
		public static final String CONTACT_INFO_SETUP_TITLE = "Contact Info Setup";
		public static final String WELCOME_TITLE = "Welcome to Biddr";
		public static final String WELCOME_MESSAGE = "Thanks for signing up! Let's start bidding...";
		public static final String BID_REQUEST_SENT = "Bid Request Send";
		public static final String BID_REQUEST_MESSAGE = "Hi, your bid request for %s in the %s auction has been sent. We'll let you know once this request has been reviewed";
		public static final String BID_REQUEST_UPDATED = "Bid Request Send";
		public static final String BID_REQUEST_UPDATED_MESSAGE = "Hi, you raised a new bid! Your new bid for %s was recently submitted and the new amount is %.2f. Your old bid for has been outbid";
		public static final String BID_REQUEST_REJECTED = "Bid Request Rejected";
		public static final String BID_REQUEST_REJECTED_MESSAGE = "Hi, your bid request for item %s was rejected";
		public static final String BID_REQUEST_ACCEPTED = "Bid Request Accepted";
		public static final String BID_REQUEST_ACCEPTED_MESSAGE = "Hi, your bid request for item %s was accepted";
	}

	public static class Auth {
		public static final String TOKEN_COOKIE = "token";
		public static final String PASSWORD_RESET = "Password Reset Request";
		public static final String PASSWORD_RESET_MESSAGE = "You have requested to reset your password. Please open this link to securely reset your password: %s";
		public static final String VERIFY_ACCOUNT = "Account Verification Request";
		public static final String VERIFY_ACCOUNT_MESSAGE = "Hi %s, please click the link below to verify your account: %s";
	}
}
