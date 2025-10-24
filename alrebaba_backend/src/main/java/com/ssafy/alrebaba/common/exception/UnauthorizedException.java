package com.ssafy.alrebaba.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GlobalException {

    public UnauthorizedException(ErrorCode<?> errorCode) { super(errorCode, HttpStatus.UNAUTHORIZED); }

    public UnauthorizedException(String message) { super(new ErrorCode<>(message), HttpStatus.UNAUTHORIZED); }

}
