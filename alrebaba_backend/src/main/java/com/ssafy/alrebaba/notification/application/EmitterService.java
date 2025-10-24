package com.ssafy.alrebaba.notification.application;

import com.ssafy.alrebaba.common.exception.BadRequestException;
import com.ssafy.alrebaba.notification.domain.EmitterRepository;
import com.ssafy.alrebaba.notification.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmitterService {

    // 연결 지속시간 1시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long memberId, String lastEventId) {
        // 고유한 ID 생성
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // 시간 초과나 비동기 요청이 안되면 자동으로 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 최초 연결 시 더미데이터가 없으면 503 오류가 발생하기 때문에 해당 더미데이터 생성
        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        // lastEventId 존재한다면 연결이 종료됨을 의미. 따라서 해당 데이터가 남아있는지 살펴본다면 남은 데이터를 전송
        if(!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithById(memberId);
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }
        return emitter;
    }

    // Kafka에서 전달받은 알람을 해당 회원의 모든 SSE 연결에 전송
    public void sendNotification(Long receiverId, NotificationMessage notificationMessage) {
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithById(receiverId);
        emitters.forEach((emitterId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .id(emitterId)
                        .data(notificationMessage));
                // 선택: 이벤트 캐시 저장 (재연결 시 누락 방지)
                emitterRepository.saveEventCache(emitterId, notificationMessage);
            } catch (Exception e) {
                emitterRepository.deleteById(emitterId);
            }
        });
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (Exception e) {
            emitterRepository.deleteById(emitterId);
            throw new BadRequestException("알림 전송 실패");
        }
    }

}
