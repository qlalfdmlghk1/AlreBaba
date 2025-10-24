package com.ssafy.alrebaba.notification.application;

import com.ssafy.alrebaba.common.exception.BadRequestException;
import com.ssafy.alrebaba.common.exception.ConflictException;
import com.ssafy.alrebaba.common.exception.NotFoundException;
import com.ssafy.alrebaba.common.storage.application.ImageUtil;
import com.ssafy.alrebaba.event.domain.Event;
import com.ssafy.alrebaba.event.domain.EventRepository;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import com.ssafy.alrebaba.notification.domain.Notification;
import com.ssafy.alrebaba.notification.domain.NotificationMessage;
import com.ssafy.alrebaba.notification.domain.NotificationRepository;
import com.ssafy.alrebaba.notification.domain.NotificationType;
import com.ssafy.alrebaba.notification.dto.request.NotificationRequest;
import com.ssafy.alrebaba.notification.dto.response.SenderInfo;
import com.ssafy.alrebaba.study.domain.Study;
import com.ssafy.alrebaba.study.domain.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationProducer notificationProducer;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final EventRepository eventRepository;
    private final ImageUtil imageUtil;

    @Transactional
    public void createNotification(NotificationRequest request) {
        Member receiver = memberRepository.findById(request.receiverId())
                .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다."));

        // 보내는 사람의 정보 조회를 별도 메서드로 분리
        SenderInfo sender = findSender(request.type(), request.referenceId());

        if(notificationRepository.existsByReceiverAndTypeAndReferenceId(receiver, request.type(), request.referenceId())) {
            throw new ConflictException("이미 알림이 존재합니다.");
        }

        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(request.type())
                .referenceId(request.referenceId())
                .build();

        notificationRepository.save(notification);

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .notificationId(notification.getNotificationId())
                .receiverId(request.referenceId())
                .type(request.type().toString())
                .senderId(request.referenceId())
                .senderName(sender.name())
                .senderImage(sender.image())
                .message(sender.message())
                .createdAt(notification.getCreatedAt())
                .build();
        notificationProducer.sendNotification(notificationMessage);
    }


    @Transactional(readOnly = true)
    public List<NotificationMessage> getAllNotificationsForUser(Long memberId) {
        List<Notification> notifications = notificationRepository.findAllByReceiverId(memberId);
        return notifications.stream()
                .map(notification -> {
                    // 저장된 알림의 type과 referenceId를 활용하여 sender 정보를 조회
                    SenderInfo sender = findSender(notification.getType(), notification.getReferenceId());
                    return NotificationMessage.of(notification, sender);
                })
                .toList();
    }

    @Transactional
    public void deleteNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("알림이 존재하지 않습니다."));

        notificationRepository.delete(notification);
    }

    @Transactional(readOnly = true)
    public Long findNotificationId(NotificationType type, Long receiverId, Long referenceId) {
        return notificationRepository.findNotificationId(type, receiverId, referenceId);
    }

    /**
     * Sender 정보 찾는 메서드
     *
     * 친구 초대: "{nickname} 님이 친구를 요청했어요"
     * 스터디 초대: "{studyName} 스터디로부터 초대장이 도착했어요"
     */
    private SenderInfo findSender(NotificationType type, Long referenceId) {
        String senderName;
        String imageUrl;
        String message;

        switch (type) {
            case FRIEND_INVITATION:
                Member member = memberRepository.findById(referenceId)
                        .orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다."));
                senderName = member.getNickname();
                imageUrl = imageUtil.getPreSignedUrl(member.getProfileImage());
                message = senderName + " 님이 친구를 요청했어요";
                break;
            case STUDY_JOIN_REQUEST:
                Study study = studyRepository.findById(referenceId)
                        .orElseThrow(() -> new NotFoundException("스터디가 존재하지 않습니다."));
                senderName = study.getStudyName();
                imageUrl = imageUtil.getPreSignedUrl(study.getImageUrl());
                message = senderName + " 스터디로부터 초대장이 도착했어요";
                break;
            case EVENT_REMINDER:
                Event event = eventRepository.findById(referenceId)
                        .orElseThrow(() -> new NotFoundException("이벤트가 존재하지 않습니다."));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
                String formattedStartTime = event.getStartTime().format(formatter);
                senderName = event.getStudy().getStudyName();
                imageUrl = imageUtil.getPreSignedUrl(event.getStudy().getImageUrl());
                message = event.getRemindBeforeMinutes() + "분 뒤에 " + senderName +
                        " 스터디에서 " + event.getEventName() + " (" + formattedStartTime + ")가 시작돼요";
                break;
            default:
                throw new BadRequestException("보내는 분의 정보를 찾을 수 없습니다.");
        }

        return new SenderInfo(referenceId, senderName, imageUrl, message);
    }

}

