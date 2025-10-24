package com.ssafy.alrebaba.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends GlobalException {

    public NotFoundException(ErrorCode<?> errorCode) { super(errorCode, HttpStatus.NOT_FOUND); }

    public NotFoundException(String message) { super(new ErrorCode<>(message), HttpStatus.NOT_FOUND); }

}
