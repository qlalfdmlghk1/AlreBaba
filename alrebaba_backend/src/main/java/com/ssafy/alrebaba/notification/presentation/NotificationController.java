package com.ssafy.alrebaba.notification.presentation;

import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.notification.application.EmitterService;
import com.ssafy.alrebaba.notification.application.NotificationService;
import com.ssafy.alrebaba.notification.domain.NotificationMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;
    private final EmitterService emitterService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @Operation(
            summary = "알림 스트림 구독",
            description = "실시간 알림 스트림을 구독하기 위한 SSE 엔드포인트입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 SSE 스트림 구독")
            }
    )
    public ResponseEntity<SseEmitter> subscribe(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @Parameter(
                    name = "Last-Event-ID",
                    description = "마지막으로 수신한 이벤트의 ID입니다. 클라이언트가 재연결 시 누락된 이벤트를 보완하기 위해 사용됩니다.",
                    required = false
            )
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        Long memberId = loginMember.getMemberId();
        return ResponseEntity.ok(emitterService.subscribe(memberId, lastEventId));
    }

    @GetMapping
    @Operation(
            summary = "사용자 알림 조회",
            description = "현재 로그인한 사용자의 모든 알림을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 알림 목록 조회")
            }
    )
    public ResponseEntity<List<NotificationMessage>> getNotifications(
            @AuthenticationPrincipal CustomMemberDetails loginMember) {
        Long memberId = loginMember.getMemberId();
        return ResponseEntity.ok(notificationService.getAllNotificationsForUser(memberId));
    }

    @DeleteMapping("/{notification-id}")
    @Operation(
            summary = "알림 삭제",
            description = "현재 로그인한 사용자의 알림을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 알림 삭제")
            }
    )
    public ResponseEntity<Void> deleteNotifications(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @PathVariable("notification-id") Long notificationId
    ) {
        Long loginId = loginMember.getMemberId();
        notificationService.deleteNotificationById(notificationId);
        return ResponseEntity.noContent().build();
    }

}
