package com.ssafy.alrebaba.friend.dto.response;

import lombok.Builder;
import java.util.List;

/**
 * 차단 목록 응답 DTO (API 명세서에 맞게 수정)
 */
@Builder
public record BlockedFriendListResponseDto(
        List<FriendResponseDto> content, // API 명세서에서 "content" 필드 사용
        boolean hasNext,                 // 다음 페이지 여부
        Long lastId                      // 마지막 데이터 ID
) {}
