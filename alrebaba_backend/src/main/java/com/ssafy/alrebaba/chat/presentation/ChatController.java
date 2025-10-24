package com.ssafy.alrebaba.chat.presentation;

import com.ssafy.alrebaba.chat.application.ChatService;
import com.ssafy.alrebaba.chat.domain.Message;
import com.ssafy.alrebaba.chat.dto.request.ChatRequest;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/send")
    public void sendMessage(@RequestBody ChatRequest chatRequest) {
        chatService.sendMessage(
                chatRequest.channelId(),
                chatRequest.senderId(),
                chatRequest.senderName(),
                chatRequest.content()
        );
    }

//    @GetMapping("/chat/{channelId}")
//    public ResponseEntity<List<Message>> getChatMessages(@PathVariable Long channelId) {
//        List<Message> messages = chatService.getChatMessagesByChannel(channelId);
//        return ResponseEntity.ok(messages);
//    }

    // 날짜별로 묶어서 특정 채널의 채팅 메시지 조회
    @GetMapping("/studies/{study-id}/channels/{channel-id}/chats")
    public Map<String, List<Message>> getGroupedChatMessages(
            @PathVariable("study-id") String studyId,
            @PathVariable("channel-id") Long channelId) {
        return chatService.getGroupedChatMessagesByChannel(channelId);
    }

    // 메시지 삭제
    @DeleteMapping("/studies/{study-id}/channels/{channel-id}/chats/{chat-id}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable("study-id") Long studyId,
            @PathVariable("channel-id") Long channelId,
            @PathVariable("chat-id") String chatId,
            @AuthenticationPrincipal CustomMemberDetails loginMember) {
        Long loginId = loginMember.getMemberId();
        chatService.deleteMessage(loginId, chatId);
        return ResponseEntity.noContent().build();
    }
}
