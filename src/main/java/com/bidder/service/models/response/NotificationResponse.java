/* (C) 2026 
bidder.app */
package com.bidder.service.models.response;

import java.util.Set;
import java.util.UUID;

import com.bidder.service.models.ContactMethod;

public record NotificationResponse(Set<ContactMethod> sentVia, UUID appUserId) {
}
