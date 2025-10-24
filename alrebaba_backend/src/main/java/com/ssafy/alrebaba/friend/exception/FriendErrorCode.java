package com.ssafy.alrebaba.friend.exception;

import lombok.Getter;

@Getter
public enum FriendErrorCode {
    FRIEND_NOT_FOUND("06000", "해당 친구 관계를 찾을 수 없습니다."),
    FRIEND_ALREADY_EXISTS("06001", "이미 친구 관계가 존재합니다."),
    FRIEND_REQUEST_NOT_FOUND("06002", "친구 요청을 찾을 수 없습니다."),
    INVALID_FRIEND_ACTION("06003", "잘못된 친구 요청입니다."),
    INVALID_FRIEND_REQUEST("06004", "자기 자신에게 친구 요청을 보낼 수 없습니다."), // 새로 추가
    MEMBER_NOT_FOUND("05400", "존재하지 않는 회원입니다.");

    private final String code;
    private final String message;

    FriendErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
