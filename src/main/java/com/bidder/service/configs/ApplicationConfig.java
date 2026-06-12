/* (C) 2026 
bidder.app */
package com.bidder.service.configs;

import java.util.Collections;

import com.bidder.service.models.AppUserPrincipal;
import com.bidder.service.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

	private final AppUserRepository appUserRepository;

	@Bean
	public UserDetailsService userDetailsService() {
		return email -> {
			var user = appUserRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid Token"));

			return new AppUserPrincipal(user.getId(), user.getEmail(), user.getPassword(), Collections.emptyList());
		};
	}

	// DAO to fetch userDetails and encode passwords
	@Bean
	public AuthenticationProvider authenticationProvider() {
		var authProvider = new DaoAuthenticationProvider(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
