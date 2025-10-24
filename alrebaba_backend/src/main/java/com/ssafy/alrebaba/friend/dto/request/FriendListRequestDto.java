package com.ssafy.alrebaba.friend.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * 친구 목록 조회 요청 DTO
 */
public record FriendListRequestDto(
        Long lastId,              // 마지막으로 조회한 친구 ID (NoOffset 페이징)
        @NotNull Integer pageSize // 페이지 크기
) {}
