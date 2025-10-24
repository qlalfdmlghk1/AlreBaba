package com.ssafy.alrebaba.friend.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 친구 삭제 요청 DTO
 */
public record FriendDeleteRequestDto(
        @NotNull Long memberId,   // 현재 로그인한 회원의 ID
        @NotNull Long friendId    // 삭제할 대상인 상대 회원의 ID
) {}
