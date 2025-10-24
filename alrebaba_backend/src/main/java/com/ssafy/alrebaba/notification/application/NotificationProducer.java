package com.ssafy.alrebaba.notification.application;

import com.ssafy.alrebaba.notification.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    @Value("${spring.kafka.topic.notification}")
    private String notificationTopic;

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    /**
     * Kafka 토픽으로 알림 메시지 전송
     * 메시지 키는 receiverId로 지정하여, 수신자 기준으로 메시지를 분배할 수 있습니다.
     */
    public void sendNotification(NotificationMessage notificationMessage) {

        try {
            log.info("📤 [Kafka Producer] Sending message: {} to topic: {}", notificationMessage, notificationTopic);
            kafkaTemplate.send(notificationTopic, String.valueOf(notificationMessage.getReceiverId()), notificationMessage);
        } catch (Exception e) {
            log.error("❌ [Kafka Producer] Failed to send message: {}", e.getMessage());
            throw new RuntimeException("알림 전송에 실패했습니다.");
        }

    }
}