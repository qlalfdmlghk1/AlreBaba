package com.ssafy.alrebaba.friend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 받은 친구 요청 목록 조회 요청 DTO
 */
public record ReceivedFriendRequestsRequestDto(
        @NotNull Long memberId,
        Long lastId,         // 마지막 조회된 데이터의 식별자 (없으면 null)
        @Min(1) int pageSize // 페이지당 요청 수, 최소 1개 이상
) {}
