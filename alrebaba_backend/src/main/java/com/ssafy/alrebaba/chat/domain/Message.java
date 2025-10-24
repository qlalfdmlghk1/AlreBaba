package com.ssafy.alrebaba.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String messageId;
    private Long channelId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;  // ✅ 변경: LocalDateTime 유지

    /**
     * Message 객체 생성 (UUID 자동 생성, 현재 시간 설정)
     */
    public static Message create(Long channelId, Long senderId, String senderName, String content) {
        return Message.builder()
                .messageId(UUID.randomUUID().toString())
                .channelId(channelId)
                .senderId(senderId)
                .senderName(senderName)
                .content(content)
                .createdAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime())    // Asia/Seoul로 시간 변경
                .build();
    }

    /**
     * Message 객체를 Chat 엔티티로 변환
     */
    public static Chat toEntity(Message message) {
        return Chat.builder()
                .chatId(message.getMessageId())
                .channelId(message.getChannelId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())  // ✅ LocalDateTime 그대로 사용
                .build();
    }

    /**
     * Chat 엔티티를 Message 객체로 변환
     */
    public static Message fromEntity(Chat chat) {
        return Message.builder()
                .messageId(chat.getChatId())
                .channelId(chat.getChannelId())
                .senderId(chat.getSenderId())
                .senderName(chat.getSenderName())
                .content(chat.getContent())
                .createdAt(chat.getCreatedAt())  // ✅ LocalDateTime 그대로 유지
                .build();
    }
}