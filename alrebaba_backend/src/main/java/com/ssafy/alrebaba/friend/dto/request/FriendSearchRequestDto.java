package com.ssafy.alrebaba.friend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 친구 검색 요청 DTO
 */
public record FriendSearchRequestDto(
        @NotBlank String search, // 검색어
        Long lastId,             // 페이징: 마지막 조회된 회원의 ID (memberId) – 첫 페이지일 경우 null
        Integer pageSize         // 페이지당 조회 건수 (기본값 20)
) {
    // 기본값 처리를 위한 compact constructor
    public FriendSearchRequestDto {
        if (pageSize == null) {
            pageSize = 20;
        }
    }
}
