package com.ssafy.alrebaba.friend.dto.response;

import lombok.Builder;
import java.util.List;

/**
 * 보낸 친구 요청 목록 응답 DTO
 */
@Builder
public record SentFriendRequestsResponseDto(
        List<FriendResponseDto> content, // API 명세에 따른 필드명: "content"
        boolean hasNext,
        Long lastId
) {}
