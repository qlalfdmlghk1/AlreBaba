package com.ssafy.alrebaba.friend.dto.request;

import jakarta.validation.constraints.NotNull;

public record BlockedFriendListRequestDto(
        @NotNull Long lastId,
        @NotNull Integer pageSize
) {}
