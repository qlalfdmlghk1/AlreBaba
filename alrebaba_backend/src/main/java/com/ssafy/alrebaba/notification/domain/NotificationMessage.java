package com.ssafy.alrebaba.notification.domain;

import com.ssafy.alrebaba.notification.dto.response.SenderInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMessage {

    private Long notificationId;
    private Long receiverId;
    private String type;
    private Long senderId;
    private String senderName;
    private String senderImage;
    private String message;
    private LocalDateTime createdAt;

    public static NotificationMessage of(Notification notification, SenderInfo sender) {
        return NotificationMessage.builder()
                .notificationId(notification.getNotificationId())
                .receiverId(notification.getReceiver().getMemberId())
                .type(notification.getType().toString())
                .senderId(sender.id())
                .senderName(sender.name())
                .senderImage(sender.image())
                .message(sender.message())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
