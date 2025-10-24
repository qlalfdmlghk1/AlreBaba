package com.ssafy.alrebaba.friend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 보낸 친구 요청 목록 조회 요청 DTO
 */
public record SentFriendRequestsRequestDto(
        @NotNull Long memberId,
        Long lastId,         // 마지막 항목의 createdAt(epoch millis) - 첫 페이지 요청 시 null 가능
        @Min(1) int pageSize // 한 페이지당 최소 1건 이상 조회
) {
    // 기본값 처리: pageSize가 0(또는 음수)이면 기본값 20을 사용하도록 함
    public SentFriendRequestsRequestDto {
        if (pageSize <= 0) {
            pageSize = 20;
        }
    }
}
