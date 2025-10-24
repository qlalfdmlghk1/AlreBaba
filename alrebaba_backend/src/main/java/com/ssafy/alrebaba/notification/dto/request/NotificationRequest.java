package com.ssafy.alrebaba.notification.dto.request;

import com.ssafy.alrebaba.notification.domain.NotificationType;
import lombok.Builder;

@Builder
public record NotificationRequest(
        Long receiverId,        // 받는 사람 ID
        NotificationType type,  // 알림 유형
        Long referenceId        // 보내는 사람 ID
) {
}
