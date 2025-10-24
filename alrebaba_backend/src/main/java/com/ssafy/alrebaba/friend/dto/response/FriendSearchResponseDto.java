package com.ssafy.alrebaba.friend.dto.response;

import lombok.Builder;
import java.util.List;

/**
 * 친구 검색 응답 DTO
 */
@Builder
public record FriendSearchResponseDto(
        List<FriendResponseDto> content, // 검색 결과 목록 (FriendResponseDto)
        boolean hasNext,
        Long lastId
) {}
