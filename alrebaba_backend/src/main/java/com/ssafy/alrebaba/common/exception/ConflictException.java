package com.ssafy.alrebaba.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends GlobalException {

    public ConflictException(ErrorCode<?> errorCode) {
        super(errorCode, HttpStatus.CONFLICT);
    }

    public ConflictException(String message) { super(new ErrorCode<>(message), HttpStatus.CONFLICT); }

}