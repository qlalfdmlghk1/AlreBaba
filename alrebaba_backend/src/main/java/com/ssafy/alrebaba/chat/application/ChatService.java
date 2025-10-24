package com.ssafy.alrebaba.chat.application;

import com.ssafy.alrebaba.chat.domain.Chat;
import com.ssafy.alrebaba.chat.domain.ChatRepository;
import com.ssafy.alrebaba.chat.domain.Message;
import com.ssafy.alrebaba.common.exception.ForbiddenException;
import com.ssafy.alrebaba.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String TOPIC = "chat-messages";
    private final KafkaProducer kafkaProducer;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ✅ 사용자가 입력한 데이터로 메시지를 생성하여 Kafka 및 WebSocket으로 전송
    public void sendMessage(Long channelId, Long senderId, String senderName, String content) {
        Message message = Message.create(channelId, senderId, senderName, content);

        // ✅ MongoDB에 저장
        Chat chat = chatRepository.save(Message.toEntity(message));

        // ✅ Kafka 전송 (Consumer 없이 사용 가능)
        kafkaProducer.send(TOPIC, Message.fromEntity(chat));

        // ✅ WebSocket을 통해 클라이언트에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + channelId, Message.fromEntity(chat));
    }

    // ✅ 날짜별로 묶어서 특정 채널의 채팅 메시지 조회
    // TODO: 페이지네이션 및 내림차순?
    public Map<String, List<Message>> getGroupedChatMessagesByChannel(Long channelId) {
        List<Chat> chatList = chatRepository.findByChannelId(channelId);

        // ✅ 날짜(YYYY-MM-DD) 기준으로 그룹화
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return chatList.stream()
                .map(Message::fromEntity)
                .collect(Collectors.groupingBy(msg -> msg.getCreatedAt().format(formatter)));
    }

    // ✅ 메시지 삭제 기능
    public void deleteMessage(Long loginId, String chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("메시지를 찾을 수 없습니다."));

        // ✅ 요청한 userId와 메시지 작성자의 senderId가 일치하는지 확인
        if (!chat.getSenderId().equals(loginId)) {
            throw new ForbiddenException("작성자만 메시지를 삭제할 수 있습니다.");
        }

        // ✅ 삭제 가능하면 MongoDB에서 메시지 삭제
        chatRepository.deleteById(chatId);

        // ✅ WebSocket을 통해 삭제된 메시지를 클라이언트들에게 알림
        messagingTemplate.convertAndSend("/topic/chat/delete", chatId);
    }

}
