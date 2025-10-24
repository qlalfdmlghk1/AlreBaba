package com.ssafy.alrebaba.auth.exception;

import lombok.Getter;

@Getter
public enum AuthErrorCode {

	REFRESH_TOKEN_NOT_FOUNDED("04000", "Can't not find refreshToken");
	private final String code;
	private final String message;

	AuthErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
