/* (C) 2026 
bidder.app */
package com.bidder.service.models;

import java.util.Collection;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class AppUserPrincipal implements UserDetails {

	private final UUID userId;
	private final String username;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;

	public AppUserPrincipal(UUID userId, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public @Nullable String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	public UUID getUserId() {
		return this.userId;
	}
}
