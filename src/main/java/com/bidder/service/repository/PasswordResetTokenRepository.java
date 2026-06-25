/* (C) 2026 
bidder.app */
package com.bidder.service.repository;

import java.util.Optional;
import java.util.UUID;

import com.bidder.service.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

	@Query("""
			select PRT
			from PasswordResetToken PRT
			where PRT.token = :token
			""")
	Optional<PasswordResetToken> findByToken(@Param("token") String token);

	@Query("""
			select PRT
			from PasswordResetToken PRT
			where PRT.appUser.id = :appUserId
			""")
	Optional<PasswordResetToken> findByAppUserId(@Param("appUserId") UUID appUserId);

}
