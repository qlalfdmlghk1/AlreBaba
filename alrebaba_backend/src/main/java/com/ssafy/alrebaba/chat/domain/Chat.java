package com.ssafy.alrebaba.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    private String chatId;  // UUID 기반의 메시지 ID

    private Long channelId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;

}