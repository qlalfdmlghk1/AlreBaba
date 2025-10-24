package com.ssafy.alrebaba.study.domain.participant;

import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.study.domain.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class StudyParticipant {

    @EmbeddedId
    private StudyParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)  // cascade 옵션 제거
    @MapsId("studyId")
    @JoinColumn(name = "study_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId") // EmbeddedId의 memberId 매핑
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_role", nullable = false)
    private ParticipantRole participantRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_status", nullable = false)
    private ParticipantStatus participantStatus;

    @Column(name = "joined_at", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime joinedAt;

    @Builder
    public StudyParticipant(Study study, Member member, ParticipantRole participantRole, ParticipantStatus participantStatus) {
        this.study = study;
        this.member = member;
        this.id = new StudyParticipantId(study.getStudyId(), member.getMemberId()); // 복합 키 설정
        this.participantRole = participantRole;
        this.participantStatus = participantStatus;
        this.joinedAt = LocalDateTime.now();
    }

    // 상태 업데이트 메서드
    public void updateStatus(ParticipantStatus status) {
        this.participantStatus = status;
    }
}
