package com.ssafy.alrebaba.chat.application;

import com.ssafy.alrebaba.chat.domain.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    public void send(String topic, Message message) {
        try {
            log.info("📤 [Kafka Producer] Sending message: {} to topic: {}", message, topic);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            log.error("❌ [Kafka Producer] Failed to send message: {}", e.getMessage());
            throw new RuntimeException("채팅 전송에 실패했습니다.");
        }
    }
}