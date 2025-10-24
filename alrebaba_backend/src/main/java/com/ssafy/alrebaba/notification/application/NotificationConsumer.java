package com.ssafy.alrebaba.notification.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.alrebaba.notification.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmitterService emitterService;
    private final ObjectMapper objectMapper;

    // Kafka 토픽 "notification-topic"에서 메시지 수신 (그룹 ID: "notification-group")
    @KafkaListener(topics = "notification-topic", groupId = "alrebaba")
    public void listen(String message) {
        try {
            // JSON 문자열을 NotificationMessage 객체로 역직렬화
            NotificationMessage notificationMessage = objectMapper.readValue(message, NotificationMessage.class);
            // 알림 전송: NotificationMessage 내 receiverId를 기준으로 SSE 구독자에게 전송
            emitterService.sendNotification(notificationMessage.getReceiverId(), notificationMessage);
        } catch (Exception e) {
            // 예외 발생 시 로깅 및 추가 처리
            e.printStackTrace();
        }
    }
}