/* (C) 2026 
bidder.app */
package com.bidder.service.controllers;

import java.util.UUID;

import com.bidder.service.models.request.LoginRequest;
import com.bidder.service.models.request.RegisterAppUserRequest;
import com.bidder.service.models.request.TokenRequest;
import com.bidder.service.models.response.ApiResponse;
import com.bidder.service.service.AuthService;
import com.bidder.service.service.JwtService;
import com.bidder.service.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.bidder.service.utils.Constants.Controller.BASE_URI;
import static com.bidder.service.utils.Constants.Controller.V1;

@Slf4j
@RestController
@RequestMapping(BASE_URI + V1 + "/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final PasswordService passwordService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<UUID>> login(@RequestBody LoginRequest request) throws BadCredentialsException {
		var response = authService.appUserLogin(request);
		var headers = jwtService.generateTokenCookieHeader(response.token());
		var loginResponse = ApiResponse.<UUID>builder().data(response.userId()).build();
		return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UUID>> register(@RequestBody RegisterAppUserRequest request) {
		var response = authService.appUserRegistration(request);
		var headers = jwtService.generateTokenCookieHeader(response.token());
		var loginResponse = ApiResponse.<UUID>builder().data(response.userId()).build();
		return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
	}

	// ToDo: add api rate limiter
	@PostMapping("/password/reset")
	public ResponseEntity<ApiResponse<Boolean>> sendPasswordResetLink(
			@RequestParam(value = "token", required = false) String token, @RequestBody TokenRequest request)
			throws IllegalAccessException {

		if (StringUtils.hasLength(token)) {
			passwordService.resetPassword(token, request);
		} else {
			passwordService.sendPasswordResetLink(request);
		}

		return new ResponseEntity<>(ApiResponse.<Boolean>builder().data(true).build(), HttpStatus.OK);
	}

	@PostMapping("/verify-account")
	public ResponseEntity<ApiResponse<Boolean>> verifyAccount(
			@RequestParam(value = "token", required = false) String token, @RequestBody TokenRequest request)
			throws IllegalAccessException {

		if (StringUtils.hasLength(token)) {
			passwordService.verifyAccount(token, request);
		} else {
			passwordService.sendAccountVerificationLink(request);
		}

		return new ResponseEntity<>(ApiResponse.<Boolean>builder().data(true).build(), HttpStatus.OK);
	}
}
