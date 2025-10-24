package com.ssafy.alrebaba.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends GlobalException {

    public ForbiddenException(ErrorCode<?> errorCode) { super(errorCode, HttpStatus.FORBIDDEN); }

    public ForbiddenException(String message) { super(new ErrorCode<>(message), HttpStatus.NOT_FOUND); }

}
