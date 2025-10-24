package com.ssafy.alrebaba.auth.dto.response;

import lombok.Builder;

@Builder
public record AccessAndRefreshToken(
        String accessToken,
        String refreshToken
) {
}
