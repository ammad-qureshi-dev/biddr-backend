/* (C) 2026 
bidder.app */
package com.bidder.service.service;

import java.util.UUID;

import com.bidder.service.models.AppUser;
import com.bidder.service.repository.AppUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserService {

	private final AppUserRepository appUserRepository;

	public AppUser getAppUserById(UUID userId) {
		return appUserRepository.findById(userId).orElseThrow();
	}

	public AppUser getAppUserByEmail(@NotNull String email) {
		return appUserRepository.findByEmail(email).orElseThrow();
	}

	public AppUser getAppUserByPhoneNumber(String phoneNumber) {
		return appUserRepository.findByPhoneNumber(phoneNumber).orElseThrow();
	}
}
