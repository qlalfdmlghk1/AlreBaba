package com.ssafy.alrebaba.code.kafka;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaSubscriptionEventProducer {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendSubscriptionEvent(String principal, String targetEventId) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(Map.of("targetEventId", targetEventId));

        messagingTemplate.convertAndSendToUser(principal, "/topic/event", payload);
        System.out.println("New subscription event sent for: " + targetEventId);
    }

}