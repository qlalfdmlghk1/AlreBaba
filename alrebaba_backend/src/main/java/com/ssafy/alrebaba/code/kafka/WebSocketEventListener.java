package com.ssafy.alrebaba.code.kafka;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ConcurrentHashMap<Long, List<String>> sessionSubscriberMap = new ConcurrentHashMap<>();
    private final KafkaSubscriptionEventProducer kafkaSubscriptionEventProducer;

    // WebSocket 연결 이벤트 감지
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal newPrincipal = headerAccessor.getUser();


        Long channelId = Long.parseLong(Objects.requireNonNull(headerAccessor.getNativeHeader("channelId")).get(0));
        String randomPrincipal = getRandomSubscriber(channelId);

        if(!sessionSubscriberMap.containsKey(channelId)){
            List<String> newList = new ArrayList<>();
            newList.add(Objects.requireNonNull(newPrincipal).getName());
            sessionSubscriberMap.put(channelId, newList);
            System.out.println("make new List");
            return;
        }

        if(randomPrincipal != null){
            kafkaSubscriptionEventProducer.sendSubscriptionEvent(randomPrincipal, newPrincipal.getName());
            System.out.println("send event");
        }
    }

    // WebSocket 연결 해제 이벤트 감지
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // sessionId 제거
        sessionSubscriberMap.values().removeIf(value -> value.equals(sessionId));
        System.out.println("Subscriber disconnected: " + sessionId);
    }

    public Object getSessionIdBySubscriberId(String subscriberId) {
        return sessionSubscriberMap.get(subscriberId);
    }

    public String getRandomSubscriber(Long key) {
        Random random = new Random();
        List<String> subscribers = sessionSubscriberMap.get(key);
        if (subscribers == null || subscribers.isEmpty()) {
            return null; // key가 없거나 List가 비어있는 경우 null 반환
        }
        return subscribers.get(random.nextInt(subscribers.size())); // 랜덤한 값 반환
    }


}
