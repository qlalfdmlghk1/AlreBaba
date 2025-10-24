package com.ssafy.alrebaba.common.configuration;

import com.ssafy.alrebaba.auth.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 헤더 어세서를 통해 프레임 정보를 가져옵니다.
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // "access" 헤더에서 토큰을 가져옵니다.
            String token = accessor.getFirstNativeHeader("access");
            if (token != null) {
                try {
                    // 토큰 만료 여부를 체크합니다.
                    if (jwtUtil.isExpired(token)) {
                        throw new IllegalArgumentException("Token expired");
                    }
                    String username = jwtUtil.getUsername(token);
                    // 필요 시 Role, memberId 등 추가 정보를 활용하여 Authentication 생성 가능
                    Authentication user = new UsernamePasswordAuthenticationToken(username, null, null);
                    accessor.setUser(user);
                } catch (Exception e) {
                    // 토큰 검증 실패 시 연결을 중단합니다.
                    throw new IllegalArgumentException("Invalid token", e);
                }
            } else {
                // 토큰이 없으면 필요한 처리: 연결을 거부하거나 기본값 설정 등
                throw new IllegalArgumentException("No access token provided");
            }
        }
        return message;
    }
}