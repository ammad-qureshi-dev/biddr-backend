/* (C) 2026 
bidder.app */
package com.bidder.service.models.request;

import java.util.Set;
import java.util.UUID;

import com.bidder.service.models.ContactMethod;
import com.bidder.service.models.NotificationType;
import org.hibernate.validator.constraints.Length;

public record NotificationRequest(UUID appUserId, String title, String content, NotificationType type,

		@Length(min = 1) Set<ContactMethod> sendVia) {
}
