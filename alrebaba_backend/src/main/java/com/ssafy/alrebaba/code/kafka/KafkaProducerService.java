package com.ssafy.alrebaba.code.kafka;

import com.ssafy.alrebaba.code.dto.request.CRDTMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, CRDTMessage> kafkaTemplate;
    private static final String CODE_TOPIC = "code-messages";

    public void sendSnapshotToSubscriber(String subscriberId, CRDTMessage crdtMessage) {
        kafkaTemplate.send("code-snapshot", subscriberId, crdtMessage); // subscriberId를 Key로 사용
        System.out.println("Snapshot sent to subscriber: " + subscriberId);
    }

    public void sendCRDToAll(CRDTMessage crdtMessage){
        kafkaTemplate.send(CODE_TOPIC, crdtMessage);
    }
}
