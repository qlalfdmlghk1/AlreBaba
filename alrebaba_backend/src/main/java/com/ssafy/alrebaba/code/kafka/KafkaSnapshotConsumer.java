package com.ssafy.alrebaba.code.kafka;

import com.ssafy.alrebaba.chat.dto.request.ChatRequest;
import com.ssafy.alrebaba.code.dto.request.CRDTMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSnapshotConsumer {

    private static final String SNAPSHOT_TOPIC = "code-snapshot";
    private final String mySubscriberId = "unique-subscriber-id"; // 고유한 구독자 ID
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketEventListener webSocketEventListener;

    @KafkaListener(topics = SNAPSHOT_TOPIC, groupId = "crdt-group")
    public void consumeSnapshot(CRDTMessage crdtMessage) {
        String subscriberId = crdtMessage.sessionId();
        String sessionId = (String) webSocketEventListener.getSessionIdBySubscriberId(subscriberId);

        messagingTemplate.convertAndSendToUser(sessionId, "/send/snapshot", crdtMessage);
    }

    private void initializeState(String snapshotData) {
        // 스냅샷 데이터를 기반으로 상태를 초기화
        System.out.println("Initializing state with snapshot: " + snapshotData);
    }
}
