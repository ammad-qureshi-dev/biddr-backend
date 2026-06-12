/* (C) 2026 
bidder.app */
package com.bidder.service.utils;

import java.util.regex.Pattern;

public class PasswordValidator {

	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

	private static final Pattern PATTERN = Pattern.compile(PASSWORD_REGEX);

	public static boolean isValidPassword(String password) {
		if (password == null) {
			return false;
		}
		return PATTERN.matcher(password).matches();
	}

	public static boolean passwordsMatch(String basePwd, String inputPwd) {
		var hashedInputPwd = HashingUtil.generateHash(inputPwd);
		return hashedInputPwd.equals(basePwd);
	}
}