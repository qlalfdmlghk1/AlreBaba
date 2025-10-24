package com.ssafy.alrebaba.common.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 경로를 지정합니다.
        config.enableSimpleBroker("/topic","/user");
        // 클라이언트가 메시지를 보낼 때 사용하는 prefix를 지정합니다.
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 실제 엔드포인트는 application.yml의 context-path(/api/v1)와 결합되어 /api/v1/ws-chat가 됩니다.
        // 순수 WebSocket(ws://) 연결을 사용하려면 .withSockJS()를 사용하지 않고, SockJS fallback을 제거합니다.
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
         .withSockJS(); // SockJS fallback 사용 시 주석 해제
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 커스텀 StompHandler를 인바운드 채널 인터셉터로 추가합니다.
        registration.interceptors(stompHandler);
    }

}

