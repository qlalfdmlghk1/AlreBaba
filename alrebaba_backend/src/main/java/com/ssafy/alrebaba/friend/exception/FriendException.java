package com.ssafy.alrebaba.friend.exception;

import com.ssafy.alrebaba.common.exception.ConflictException;
import com.ssafy.alrebaba.common.exception.ErrorCode;
import org.apache.coyote.BadRequestException;

public class FriendException {

    public static class FriendConflictException extends ConflictException {
        public FriendConflictException(FriendErrorCode errorCode, String value) {
            super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage(), value));
        }
    }

    public static class FriendBadRequestException extends BadRequestException {
        public FriendBadRequestException(FriendErrorCode errorCode) {
            super(String.valueOf(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage())));
        }
    }
}
