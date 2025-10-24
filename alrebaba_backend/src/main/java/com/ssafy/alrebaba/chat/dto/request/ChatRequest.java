package com.ssafy.alrebaba.chat.dto.request;

import lombok.Builder;

@Builder
public record ChatRequest(
        Long channelId,
        Long senderId,
        String senderName,
        String content
) {}
