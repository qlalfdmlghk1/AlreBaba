package com.ssafy.alrebaba.auth.exception;

import org.apache.coyote.BadRequestException;

import com.ssafy.alrebaba.common.exception.ErrorCode;

public class AuthException {

	public static class AuthBadRequestException extends BadRequestException {
		public AuthBadRequestException(AuthErrorCode errorCode) {
			super(String.valueOf(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage())));
		}
	}

}
