/* (C) 2026 
bidder.app */
package com.bidder.service.service;

import java.util.*;

import com.bidder.service.mappers.NotificationMapper;
import com.bidder.service.models.AppUser;
import com.bidder.service.models.ContactMethod;
import com.bidder.service.models.Notification;
import com.bidder.service.models.NotificationType;
import com.bidder.service.models.request.NotificationRequest;
import com.bidder.service.models.response.NotificationResponse;
import com.bidder.service.repository.NotificationRepository;
import com.bidder.service.service.notification.EmailSenderService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bidder.service.utils.Constants.Messages.CONTACT_INFO_SETUP_TITLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final AppUserService appUserService;
	private final NotificationRepository notificationRepository;
	private final EmailSenderService emailSenderService;

	public static final Set<ContactMethod> ALL_NOTIFICATION_METHODS = Set.of(ContactMethod.APP, ContactMethod.EMAIL,
			ContactMethod.MOBILE);

	@Transactional
	public NotificationResponse sendNotification(NotificationRequest request) {
		var appUser = appUserService.getAppUserById(request.appUserId());

		var notification = NotificationMapper.requestToEntity(request);
		notification.setRecipient(appUser);

		if (appUser.getEmail() == null && appUser.getPhoneNumber() == null) {
			var actionReqNotification = generateContactSetupNotification(appUser);
			notificationRepository.save(actionReqNotification);
		}

		if (request.sendVia().contains(ContactMethod.APP)) {
			sendAppNotification(request, appUser);
		}

		if (request.sendVia().contains(ContactMethod.MOBILE) && appUser.getPhoneNumber() != null) {
			sendSmsNotification(request, appUser);
		}

		if (request.sendVia().contains(ContactMethod.EMAIL) && appUser.getEmail() != null) {
			emailSenderService.sendEmailNotification(request, appUser);
		}

		// ToDo: include status of notifcation if mobile / email
		var response = new NotificationResponse(request.sendVia(), appUser.getId());
		log.info("notification-service -- notification sent: {}", response);

		return response;
	}

	public Notification generateContactSetupNotification(AppUser appUser) {
		return Notification.builder().title(CONTACT_INFO_SETUP_TITLE)
				.content("Please add at least one mode of contact (email or mobile) to retrieve real-time updates.")
				.sentVia(ContactMethod.APP).recipient(appUser).type(NotificationType.ACTION_REQUIRED).build();
	}

	private void sendAppNotification(@NotNull NotificationRequest request, AppUser recipient) {
		var notification = NotificationMapper.requestToEntity(request);
		notification.setRecipient(recipient);
		notification.setSentVia(ContactMethod.APP);
		notificationRepository.save(notification);

		log.info("App Notification sent to {}", recipient.getId());
	}

	private void sendSmsNotification(@NotNull NotificationRequest request, AppUser recipient) {
		throw new RuntimeException("Method not implemented yet");
	}
}
