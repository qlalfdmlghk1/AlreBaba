package com.ssafy.alrebaba.code.kafka;

import com.ssafy.alrebaba.code.dto.request.CRDTMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSubscriptionEventListener {

    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = "subscription-event", groupId = "publisher-group")
    public void handleSubscriptionEvent(CRDTMessage crdtMessage) {
        String subscriberId = crdtMessage.sessionId();
        System.out.println("New subscriber detected: " + subscriberId);

        // 최신 스냅샷 데이터를 새로운 구독자에게만 전송
        kafkaProducerService.sendSnapshotToSubscriber(subscriberId, crdtMessage);
    }

    private String generateSnapshotData() {
        // 최신 데이터를 생성하는 로직
        return "{ \"key\": \"value\", \"timestamp\": " + System.currentTimeMillis() + " }";
    }
}
