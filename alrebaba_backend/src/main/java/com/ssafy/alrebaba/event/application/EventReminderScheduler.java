package com.ssafy.alrebaba.event.application;

import com.ssafy.alrebaba.common.exception.NotFoundException;
import com.ssafy.alrebaba.event.domain.Event;
import com.ssafy.alrebaba.event.domain.EventRepository;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import com.ssafy.alrebaba.notification.application.NotificationProducer;
import com.ssafy.alrebaba.notification.domain.Notification;
import com.ssafy.alrebaba.notification.domain.NotificationMessage;
import com.ssafy.alrebaba.notification.domain.NotificationRepository;
import com.ssafy.alrebaba.notification.domain.NotificationType;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventReminderScheduler {

    private final EventRepository eventRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final NotificationProducer notificationProducer;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    /**
     * 매 분마다 실행하여 이벤트 시작 remindBeforeMinutes 전 알림 전송
     * 조건: 아직 알림 전송되지 않은 이벤트 중, 현재 시간이 이벤트 시작 시간 - remindBeforeMinutes보다 이후인 경우
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void sendEventReminders() {
        LocalDateTime now = LocalDateTime.now();
        // 아직 알림 전송되지 않았고, remindBeforeMinutes가 설정된 이벤트 조회
        List<Event> events = eventRepository.findByReminderSentFalseAndRemindBeforeMinutesIsNotNull();

        // 일정 시작 시간을 포맷하기 위한 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        for (Event event : events) {
            // 알림 전송 시각 계산: 이벤트 시작 시간에서 remindBeforeMinutes 만큼 차감
            LocalDateTime reminderTime = event.getStartTime().minusMinutes(event.getRemindBeforeMinutes());
            // 현재 시각이 알림 전송 시각 이후인 경우에만 알림 전송 진행
            if (!now.isBefore(reminderTime)) {
                // StudyParticipantRepository의 메서드를 활용하여 스터디에 가입한 회원 ID 목록 조회
                List<Long> participantIds = studyParticipantRepository.findJoinedMemberIdsByStudyId(event.getStudy().getStudyId());
                for (Long memberId : participantIds) {
                    // 해당 회원 정보 조회 (DB 저장을 위해)
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다."));

                    // 일정 시작 시간을 예쁘게 포맷 (예: "2025.02.11 12:44")
                    String formattedStartTime = event.getStartTime().format(formatter);

                    // 요청한 메시지 포맷: "{remindBeforeMinutes}분 뒤에 {studyName} 스터디에서 {eventName}(일정 시작: {formattedStartTime})가 시작돼요"
                    String message = event.getRemindBeforeMinutes() + "분 뒤에 "
                            + event.getStudy().getStudyName() + " 스터디에서 "
                            + event.getEventName() + "(" + formattedStartTime + ")가 시작돼요";

                    // Notification 엔티티 생성 및 DB 저장
                    Notification notification = Notification.builder()
                            .receiver(member)
                            .type(NotificationType.EVENT_REMINDER)
                            .referenceId(event.getEventId())
                            .build();
                    notificationRepository.save(notification);

                    // 저장된 Notification을 바탕으로 Kafka 전송용 NotificationMessage 구성
                    NotificationMessage notificationMessage = NotificationMessage.builder()
                            .notificationId(notification.getNotificationId())
                            .receiverId(memberId)
                            .type(NotificationType.EVENT_REMINDER.toString())
                            .senderName(event.getStudy().getStudyName())
                            .senderImage(event.getStudy().getImageUrl())
                            .message(message)
                            .createdAt(notification.getCreatedAt())
                            .build();
                    // Kafka를 통해 알림 메시지 전송
                    notificationProducer.sendNotification(notificationMessage);
                }
                // 알림 전송 후 중복 전송 방지를 위해 이벤트의 플래그 업데이트
                event.markReminderSent();
            }
        }
    }
}
