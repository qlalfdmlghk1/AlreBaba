package com.ssafy.alrebaba.notification.domain;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String emitterId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithById(Long memberId);

    Map<String, Object> findAllEventCacheStartWithById(Long memberId);

    void deleteById(String emitterId);

    void deleteAllEmitterStartWithById(Long memberId);

    void deleteAllEventCacheStartWithById(Long memberId);

}

