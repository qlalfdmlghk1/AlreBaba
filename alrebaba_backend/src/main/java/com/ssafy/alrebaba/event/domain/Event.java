package com.ssafy.alrebaba.event.domain;

import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.study.domain.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Member createdBy;

    @Column(name = "event_name", nullable = false, length = 50)
    private String eventName;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime startTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "remind_before_minutes")
    private Integer remindBeforeMinutes;

    @Column(nullable = false, length = 7)
    private String color;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp")
    @CreatedDate
    private LocalDateTime createdAt;

    // 알림 전송 여부, 기본값 false
    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Builder
    public Event(Study study, Member createdBy, String eventName, String description, LocalDateTime startTime, int durationMinutes, Integer remindBeforeMinutes, String color) {
        this.study = study;
        this.createdBy = createdBy;
        this.eventName = eventName;
        this.description = description;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.remindBeforeMinutes = remindBeforeMinutes;
        this.color = color;
        this.reminderSent = false;
    }

    public void update(String eventName, String description, LocalDateTime startTime, int durationMinutes, Integer remindBeforeMinutes, String color ) {
        this.eventName = eventName;
        this.description = description;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.remindBeforeMinutes = remindBeforeMinutes;
        this.color = color;
    }

    // 알림 전송 후 플래그 업데이트
    public void markReminderSent() {
        this.reminderSent = true;
    }

}
