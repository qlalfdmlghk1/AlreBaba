package com.ssafy.alrebaba.member.exception;

import com.ssafy.alrebaba.common.exception.ConflictException;
import com.ssafy.alrebaba.common.exception.ErrorCode;
import org.apache.coyote.BadRequestException;

public class MemberException {

    public static class MemberConflictException extends ConflictException {
        public MemberConflictException(MemberErrorCode errorCode, String value) {
            super(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage(), value));
        }
    }

    public static class MemberBadRequestException extends BadRequestException {
        public MemberBadRequestException(MemberErrorCode errorCode) {
            super(String.valueOf(new ErrorCode<>(errorCode.getCode(), errorCode.getMessage())));
        }
    }

}