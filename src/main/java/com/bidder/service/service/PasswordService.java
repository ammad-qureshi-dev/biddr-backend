/* (C) 2026 
bidder.app */
package com.bidder.service.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.bidder.service.models.*;
import com.bidder.service.models.request.NotificationRequest;
import com.bidder.service.models.request.TokenRequest;
import com.bidder.service.repository.AccessTokenRepository;
import com.bidder.service.repository.AppUserRepository;
import com.bidder.service.utils.HashingUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.bidder.service.utils.Constants.Auth.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

	@Value("${CLIENT_URL}")
	private String clientUrl;

	@Value("${auth.registration-pwd.allow-simple}")
	private boolean allowSimplePassword;

	private static final int PASSWORD_RESET_EXPIRY_MINUTES = 15;
	private static final int ACCOUNT_VERIFICATION_EXPIRY_MINUTES = 60;
	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
	private static final Pattern PATTERN = Pattern.compile(PASSWORD_REGEX);

	private final AppUserService appUserService;
	private final AppUserRepository appUserRepository;
	private final NotificationService notificationService;
	private final AccessTokenRepository accessTokenRepository;

	public void sendPasswordResetLink(TokenRequest request) {
		if (ContactMethod.EMAIL.equals(request.contactMethod())) {
			sendPasswordLinkByEmail(request.email());
		} else if (ContactMethod.MOBILE.equals(request.contactMethod())) {
			throw new RuntimeException("Password Reset by mobile not implemented");
		} else {
			throw new RuntimeException("Method selected does not exist");
		}
	}

	public void verifyAccount(String token, TokenRequest request) throws IllegalAccessException {
		var hashedToken = HashingUtil.generateHash(token);
		AppUser appUser = getAppUserByContactMethod(request);

		var accessToken = accessTokenRepository.findByToken(hashedToken, TokenType.VERIFICATION);
		validateClientToken(accessToken, token, TokenType.VERIFICATION);

		appUser.setVerifiedAccount(true);
		appUserRepository.save(appUser);
		log.info("User account successfully verified");

		accessTokenRepository.delete(accessToken.get());

		notificationService.sendNotification(
				new NotificationRequest(appUser.getId(), "Account Verified", "Your account was verified",
						NotificationType.SUCCESS, Set.of(ContactMethod.APP, request.contactMethod())));
	}

	public void sendAccountVerificationLink(TokenRequest request) {
		if (ContactMethod.EMAIL.equals(request.contactMethod())) {
			sendAccountVerificationLinkByEmail(request.email());
		} else if (ContactMethod.MOBILE.equals(request.contactMethod())) {
			throw new RuntimeException("Password Reset by mobile not implemented");
		} else {
			throw new RuntimeException("Method selected does not exist");
		}
	}

	private void sendAccountVerificationLinkByEmail(String email) {
		var appUser = appUserRepository.findByEmail(email);

		if (appUser.isEmpty()) {
			return;
		}

		var token = generateAndSaveToken(appUser.get(), TokenType.VERIFICATION, ACCOUNT_VERIFICATION_EXPIRY_MINUTES);

		if (token.isEmpty()) {
			log.info("Account Verification Token already exists for {}", email);
			return;
		}

		var resetUrl = clientUrl + "/verify-account?token=" + token.get();
		var notificationMessage = String.format(VERIFY_ACCOUNT_MESSAGE, appUser.get().getFirstName(), resetUrl);

		notificationService.sendNotification(new NotificationRequest(appUser.get().getId(), VERIFY_ACCOUNT,
				notificationMessage, NotificationType.ACTION_REQUIRED, new HashSet<>(List.of(ContactMethod.EMAIL))));
	}

	public void resetPassword(String token, TokenRequest request) throws IllegalAccessException {
		var hashedToken = HashingUtil.generateHash(token);
		AppUser appUser = getAppUserByContactMethod(request);

		var passwordResetToken = accessTokenRepository.findByToken(hashedToken, TokenType.PASSWORD_RESET);
		validateClientToken(passwordResetToken, token, TokenType.PASSWORD_RESET);

		var hashedPassword = HashingUtil.generateHash(request.password());
		appUser.setPassword(hashedPassword);
		appUserRepository.save(appUser);
		log.info("Password successfully reset");

		accessTokenRepository.delete(passwordResetToken.get());

		notificationService
				.sendNotification(new NotificationRequest(appUser.getId(), "Password Reset", "Your password was reset",
						NotificationType.SUCCESS, Set.of(ContactMethod.APP, request.contactMethod())));
	}

	public boolean isValidPassword(String password) {
		if (allowSimplePassword) {
			return true;
		}

		if (password == null) {
			return false;
		}

		return PATTERN.matcher(password).matches();
	}

	public static boolean passwordsMatch(String basePwd, String inputPwd) {
		var hashedInputPwd = HashingUtil.generateHash(inputPwd);
		return hashedInputPwd.equals(basePwd);
	}

	private void sendPasswordLinkByEmail(@NotNull String email) {
		var appUser = appUserRepository.findByEmail(email);

		if (appUser.isEmpty()) {
			return;
		}

		var token = generateAndSaveToken(appUser.get(), TokenType.PASSWORD_RESET, PASSWORD_RESET_EXPIRY_MINUTES);

		if (token.isEmpty()) {
			log.info("Password Reset Token already exists for {}", email);
			return;
		}

		var resetUrl = clientUrl + "/password/reset?token=" + token.get();
		var notificationMessage = String.format(PASSWORD_RESET_MESSAGE, resetUrl);

		notificationService.sendNotification(new NotificationRequest(appUser.get().getId(), PASSWORD_RESET,
				notificationMessage, NotificationType.ACTION_REQUIRED, new HashSet<>(List.of(ContactMethod.EMAIL))));
	}

	/**
	 * Generates and saves a hashed token for the user. If a token already exists,
	 * it will return nothing. A new token will return a value
	 *
	 * @param appUser
	 * @return token if newly created, return nothing if a token already exists and
	 *         is unexpired
	 */
	private Optional<String> generateAndSaveToken(AppUser appUser, TokenType tokenType, int expiryMinutes) {
		var currentToken = accessTokenRepository.findByAppUserId(appUser.getId(), tokenType);

		if (currentToken.isPresent()) {
			var token = currentToken.get();

			// If the current token is expired, delete and create a new one
			if (token.isExpired()) {
				accessTokenRepository.delete(token);
			} else {
				return Optional.empty();
			}
		}

		var token = HashingUtil.generateToken();
		var passwordResetToken = AccessToken.builder().appUser(appUser).token(HashingUtil.generateHash(token))
				.expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes)).tokenType(tokenType).build();
		accessTokenRepository.save(passwordResetToken);
		return Optional.of(token);
	}

	private AppUser getAppUserByContactMethod(TokenRequest request) {
		if (ContactMethod.EMAIL.equals(request.contactMethod())) {
			return appUserService.getAppUserByEmail(request.email());
		} else if (ContactMethod.MOBILE.equals(request.contactMethod())) {
			return appUserService.getAppUserByPhoneNumber(request.phoneNumber());
		} else {
			throw new RuntimeException("Method selected does not exist, user not found");
		}
	}

	private void validateClientToken(Optional<AccessToken> storedToken, String clientToken, TokenType tokenType)
			throws IllegalAccessException {
		if (storedToken.isEmpty()) {
			log.error("Token not found for incoming token: {}", clientToken);
			throw new IllegalAccessException("Invalid Access");
		}

		var token = storedToken.get();
		if (token.isExpired()) {
			log.error("Stored token expired for incoming token: {}", clientToken);
			throw new IllegalAccessException("Link Expired");
		}

		var hashedIncomingToken = HashingUtil.generateHash(clientToken);
		if (!token.getToken().equals(hashedIncomingToken) || !token.getTokenType().equals(tokenType)) {
			log.error("Tokens don't match{}", clientToken);
			throw new IllegalAccessException("Invalid Token");
		}
	}
}
