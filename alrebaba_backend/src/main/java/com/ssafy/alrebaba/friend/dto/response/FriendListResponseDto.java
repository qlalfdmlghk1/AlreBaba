package com.ssafy.alrebaba.friend.dto.response;

import lombok.Builder;
import java.util.List;

/**
 * 친구 목록 응답 DTO
 */
@Builder
public record FriendListResponseDto(
        List<FriendResponseDto> content, // 친구 목록
        boolean hasNext,                 // 다음 페이지 여부
        Long lastId                      // 마지막 친구 ID
) {}
