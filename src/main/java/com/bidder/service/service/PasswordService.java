/* (C) 2026 
bidder.app */
package com.bidder.service.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.bidder.service.models.AppUser;
import com.bidder.service.models.ContactMethod;
import com.bidder.service.models.NotificationType;
import com.bidder.service.models.PasswordResetToken;
import com.bidder.service.models.request.NotificationRequest;
import com.bidder.service.models.request.ResetPasswordRequest;
import com.bidder.service.repository.AppUserRepository;
import com.bidder.service.repository.PasswordResetTokenRepository;
import com.bidder.service.utils.HashingUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.bidder.service.utils.Constants.Auth.PASSWORD_RESET;
import static com.bidder.service.utils.Constants.Auth.PASSWORD_RESET_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

	@Value("${CLIENT_URL}")
	private String clientUrl;

	@Value("${auth.registration-pwd.allow-simple}")
	private boolean allowSimplePassword;

	private static final int PASSWORD_RESET_EXPIRY_MINUTES = 15;
	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
	private static final Pattern PATTERN = Pattern.compile(PASSWORD_REGEX);

	private final AppUserService appUserService;
	private final AppUserRepository appUserRepository;
	private final NotificationService notificationService;
	private final PasswordResetTokenRepository passwordResetTokenRepository;

	public void sendPasswordResetLink(ResetPasswordRequest request) {
		if (ContactMethod.EMAIL.equals(request.resetMethod())) {
			sendPasswordLinkByEmail(request.email());
		} else if (ContactMethod.MOBILE.equals(request.resetMethod())) {
			throw new RuntimeException("Password Reset by mobile not implemented");
		} else {
			throw new RuntimeException("Method selected does not exist");
		}
	}

	public void resetPassword(String token, ResetPasswordRequest request) throws IllegalAccessException {
		var hashedToken = HashingUtil.generateHash(token);
		AppUser appUser = null;

		if (ContactMethod.EMAIL.equals(request.resetMethod())) {
			appUser = appUserService.getAppUserByEmail(request.email());
		} else if (ContactMethod.MOBILE.equals(request.resetMethod())) {
			appUser = appUserService.getAppUserByPhoneNumber(request.phoneNumber());
		} else {
			throw new RuntimeException("Method selected does not exist, user not found");
		}

		if (appUser == null) {
			log.error("Failed to reset password, user not found with request: {}", request);
			throw new IllegalStateException("User not found");
		}

		var passwordResetToken = passwordResetTokenRepository.findByToken(hashedToken);
		if (passwordResetToken.isEmpty()) {
			log.error("Failed to reset password, password reset token not found");
			throw new IllegalAccessException("Invalid link");
		}

		if (LocalDateTime.now().isAfter(passwordResetToken.get().getExpiresAt())) {
			log.error("Failed to reset password, password reset token expired");
			throw new IllegalAccessException("Link Expired");
		}

		var hashedPassword = HashingUtil.generateHash(request.password());
		appUser.setPassword(hashedPassword);
		log.info("Password successfully reset");

		passwordResetTokenRepository.delete(passwordResetToken.get());

		notificationService.sendNotification(new NotificationRequest(appUser.getId(), "Password Reset",
				"Your password was reset", NotificationType.SUCCESS, Set.of(ContactMethod.APP, request.resetMethod())));
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

		var token = generateAndSavePasswordResetToken(appUser.get());

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
	 * Generates and saves a hashed password reset token for the user. If a token
	 * already exists, it will return nothing. A new token will return a value
	 * 
	 * @param appUser
	 * @return token if newly created, return nothing if a token already exists and
	 *         is unexpired
	 */
	private Optional<String> generateAndSavePasswordResetToken(AppUser appUser) {
		var currentToken = passwordResetTokenRepository.findByAppUserId(appUser.getId());

		if (currentToken.isPresent()) {
			var token = currentToken.get();

			// If the current token is expired, delete and create a new one
			if (token.isExpired()) {
				passwordResetTokenRepository.delete(token);
			} else {
				return Optional.empty();
			}
		}

		var token = HashingUtil.generateToken();
		var passwordResetToken = PasswordResetToken.builder().appUser(appUser).token(HashingUtil.generateHash(token))
				.expiresAt(LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRY_MINUTES)).build();
		passwordResetTokenRepository.save(passwordResetToken);
		return Optional.of(token);

	}

}
