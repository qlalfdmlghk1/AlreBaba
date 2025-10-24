package com.ssafy.alrebaba.study.dto.response;

import lombok.Builder;

@Builder
public record ParticipantResponse(
        Long memberId,
        String nickname,
        String role
) {
}
