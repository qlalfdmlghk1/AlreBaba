package com.ssafy.alrebaba.code.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CodeResponse(
        Long codeId,
        Long channelId,
        String platform,
        String title,
        String language,
        LocalDateTime createAt,
        Long memberId,
        Long problemId
) {
}
