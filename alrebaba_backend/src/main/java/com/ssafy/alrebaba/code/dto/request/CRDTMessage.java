package com.ssafy.alrebaba.code.dto.request;

public record CRDTMessage(
        String sessionId,
        Long channelId,
        String content
) {
}
