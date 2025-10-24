package com.ssafy.alrebaba.notification.domain;

import com.ssafy.alrebaba.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;    // 받는 사람

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Long referenceId;   // 보내는 사람 ID

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 생성자 추가 (비즈니스 생성 메서드)
    @Builder
    public Notification(Member receiver, NotificationType type, Long referenceId) {
        this.receiver = receiver;
        this.type = type;
        this.referenceId = referenceId;
    }
}
