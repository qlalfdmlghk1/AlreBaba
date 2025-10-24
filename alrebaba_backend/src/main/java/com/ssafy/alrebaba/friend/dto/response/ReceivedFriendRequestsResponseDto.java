package com.ssafy.alrebaba.friend.dto.response;

import lombok.Builder;
import java.util.List;

/**
 * 받은 친구 요청 목록 응답 DTO
 */
@Builder
public record ReceivedFriendRequestsResponseDto(
        List<FriendResponseDto> content, // 실제 친구 요청 목록
        boolean hasNext,                 // 다음 페이지가 존재하면 true
        Long lastId                      // 현재 페이지 마지막 친구 요청의 기준 값 (다음 페이지 조회 시 lastId로 사용)
) {}
