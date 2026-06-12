/* (C) 2026 
bidder.app */
package com.bidder.service.service;

import java.util.HashSet;
import java.util.List;

import com.bidder.service.mappers.AppUserMapper;
import com.bidder.service.models.AppUser;
import com.bidder.service.models.AppUserPrincipal;
import com.bidder.service.models.ContactMethod;
import com.bidder.service.models.NotificationType;
import com.bidder.service.models.request.LoginRequest;
import com.bidder.service.models.request.NotificationRequest;
import com.bidder.service.models.request.RegisterAppUserRequest;
import com.bidder.service.models.response.AuthResponse;
import com.bidder.service.repository.AppUserRepository;
import com.bidder.service.utils.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bidder.service.utils.Constants.ExceptionMessages.INVALID_CREDENTIALS;
import static com.bidder.service.utils.Constants.Messages.WELCOME_MESSAGE;
import static com.bidder.service.utils.Constants.Messages.WELCOME_TITLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final AppUserRepository appUserRepository;
	private final JwtService jwtService;
	private final NotificationService notificationService;

	@Transactional
	public AuthResponse appUserRegistration(RegisterAppUserRequest request) {
		validateAppUserRequest(request);

		var user = AppUserMapper.requestToEntity(request);
		appUserRepository.save(user);

		var token = jwtService.generateToken(
				AppUserPrincipal.builder().userId(user.getId()).username(getUsernameForSecurity(request)).build());

		notificationService.sendNotification(
				new NotificationRequest(user.getId(), WELCOME_TITLE, WELCOME_MESSAGE, NotificationType.SUCCESS,
						new HashSet<>(List.of(ContactMethod.APP, ContactMethod.EMAIL, ContactMethod.MOBILE))));

		return new AuthResponse(token, user.getId());
	}

	public AuthResponse appUserLogin(LoginRequest request) throws AuthenticationException {
		AppUser appUser;

		if (request.email() != null) {
			var userByEmail = appUserRepository.findByEmail(request.email());
			if (userByEmail.isEmpty()) {
				throw new AuthenticationException(INVALID_CREDENTIALS);
			}
			appUser = userByEmail.get();
		} else {
			var userByPhone = appUserRepository.findByPhoneNumber(request.phoneNumber());
			if (userByPhone.isEmpty()) {
				throw new AuthenticationException(INVALID_CREDENTIALS);
			}
			appUser = userByPhone.get();
		}

		if (!PasswordValidator.passwordsMatch(appUser.getPassword(), request.password())) {
			throw new AuthenticationException(INVALID_CREDENTIALS);
		}

		var token = jwtService.generateToken(
				AppUserPrincipal.builder().userId(appUser.getId()).username(getUsernameForSecurity(request)).build());

		return new AuthResponse(token, appUser.getId());
	}

	private void validateAppUserRequest(RegisterAppUserRequest request) {
		if (request.email() == null && request.phoneNumber() == null) {
			throw new IllegalStateException("Provide either email or phone-number");
		}

		if (!PasswordValidator.isValidPassword(request.password())) {
			throw new IllegalStateException("Invalid password format");
		}

		if (request.email() != null && appUserRepository.emailExists(request.email())) {
			throw new IllegalStateException("User already exists with that email");
		}

		if (request.fullName() == null) {
			throw new IllegalStateException("Name required");
		}
	}

	private static String getUsernameForSecurity(Object request) {
		if (request instanceof RegisterAppUserRequest req) {
			return req.email() != null ? req.email() : req.phoneNumber();
		} else {
			var req = (LoginRequest) request;
			return req.email() != null ? req.email() : req.phoneNumber();
		}
	}
}
