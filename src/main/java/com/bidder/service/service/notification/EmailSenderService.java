/* (C) 2026 
bidder.app */
package com.bidder.service.service.notification;

import com.bidder.service.models.AppUser;
import com.bidder.service.models.request.NotificationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderService {

	@Value("${MAIL_USERNAME}")
	private String emailFrom;

	private final JavaMailSender emailSender;
	private static final int RETRY_LIMIT = 3;

	public void sendEmailNotification(@NotNull NotificationRequest request, AppUser recipient) {
		int tries = 0;

		var email = recipient.getEmail();
		if (!StringUtils.hasLength(email)) {
			throw new IllegalStateException("Email cannot be sent, no email provided");
		}

		while (true) {
			try {
				tries++;
				emailSender.send(generateSimpleMessage(request, email));

				log.info("Email Notification sent to {}", recipient.getId());
				break;
			} catch (RuntimeException e) {
				log.error("Failed to send notification on try #{}. Retrying...", tries, e);

				if (tries >= RETRY_LIMIT) {
					log.error("Failed to send notification, retries exceeded", e);
					throw e;
				}
			}
		}
	}

	private SimpleMailMessage generateSimpleMessage(@Valid @NotNull NotificationRequest request, String email) {
		var message = new SimpleMailMessage();

		message.setTo(email);
		message.setSubject(request.title());
		message.setText(request.content());
		message.setFrom(emailFrom);

		return message;
	}
}
