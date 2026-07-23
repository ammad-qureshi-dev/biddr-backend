/* (C) 2026 
bidder.app */
package com.bidder.service.controllers;

import java.util.Map;

import com.bidder.service.models.AppUserPrincipal;
import com.bidder.service.models.response.ApiResponse;
import com.bidder.service.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.ContactType;
import models.dtos.request.PreferredContactMethodRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.bidder.service.utils.Constants.Controller.BASE_URI;
import static com.bidder.service.utils.Constants.Controller.V1;

@RestController
@RequestMapping(BASE_URI + V1 + "/app-user")
@RequiredArgsConstructor
@Slf4j
public class AppUserController {

	private final AppUserService appUserService;

	@PutMapping("/contacts/preferred-contact-method")
	public ResponseEntity<ApiResponse<Boolean>> updateContactType(@RequestBody PreferredContactMethodRequest request,
			@AuthenticationPrincipal AppUserPrincipal appUser) {
		var appUserId = appUser.getUserId();
		appUserService.updatePreferredContactMethod(request, appUserId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Boolean>builder().data(true).build());
	}

	@GetMapping("/contacts")
	public ResponseEntity<ApiResponse<Map<ContactType, String>>> getContactMethods(
			@AuthenticationPrincipal AppUserPrincipal appUser) {
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Map<ContactType, String>>builder()
				.data(appUserService.getContactMethods(appUser.getUserId())).build());
	}

}
