package com.ssafy.alrebaba.friend.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 친구 요청 보내기 DTO
 */
public record FriendRequestDto(
        @NotNull Long requestId,
        @NotNull Long acceptId
) {}
