package com.ssafy.alrebaba.friend.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 친구 요청 응답 DTO
 */
@Builder
public record FriendRequestResponseDto(
        Long requestId,
        Long acceptId,
        String status,
        LocalDateTime createdAt
) {}
