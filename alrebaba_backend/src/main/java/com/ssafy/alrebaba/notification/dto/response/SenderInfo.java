package com.ssafy.alrebaba.notification.dto.response;

public record SenderInfo(
        Long id,
        String name,
        String image,
        String message
) {
}
