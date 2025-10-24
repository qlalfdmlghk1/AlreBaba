package com.ssafy.alrebaba.friend.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 친구 상태 업데이트 DTO
 */
public record FriendStatusUpdateRequestDto(
        @NotNull Long memberId, // 현재 로그인한 회원의 ID
        @NotNull Long friendId, // 상대 회원의 ID
        @NotNull String status  // 변경할 상태 (예: "FOLLOWING", "BANNED" 등)
) {}
