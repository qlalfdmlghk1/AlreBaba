package com.ssafy.alrebaba.friend.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 친구 상태 업데이트 응답 DTO
 */
@Builder
public record FriendStatusUpdateResponseDto(
        Long requestId,
        String status,
        LocalDateTime updatedAt
) {}
