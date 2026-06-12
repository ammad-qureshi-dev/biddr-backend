/* (C) 2026 
bidder.app */
package com.bidder.service.mappers;

import com.bidder.service.models.Notification;
import com.bidder.service.models.request.NotificationRequest;

public class NotificationMapper {

	public static Notification requestToEntity(NotificationRequest request) {
		return Notification.builder().type(request.type()).content(request.content()).title(request.title()).build();
	}
}
