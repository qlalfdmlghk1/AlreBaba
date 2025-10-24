package com.ssafy.alrebaba.notification.domain;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId,sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId,event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithById(Long memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithById(Long memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public void deleteAllEmitterStartWithById(Long memberId) {
        emitters.forEach((emitterId, emitter) -> {
            if(emitterId.startsWith(memberId.toString())) {
                emitters.remove(emitterId);
            }
        });
    }

    @Override
    public void deleteAllEventCacheStartWithById(Long memberId) {
        eventCache.forEach((emitterId, event) -> {
            if(emitterId.startsWith(memberId.toString())) {
                emitters.remove(emitterId);
            }
        });
    }

}
