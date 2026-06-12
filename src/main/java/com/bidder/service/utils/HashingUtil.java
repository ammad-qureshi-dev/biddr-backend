/* (C) 2026 
bidder.app */
package com.bidder.service.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HashingUtil {

	public static String generateHash(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(s.getBytes());
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Hash failed");
		}
	}
}