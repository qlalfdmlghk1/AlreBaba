package com.ssafy.alrebaba.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends GlobalException {

    public BadRequestException(ErrorCode<?> errorCode) {
        super(errorCode, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message) { super(new ErrorCode<>(message), HttpStatus.BAD_REQUEST); }

}