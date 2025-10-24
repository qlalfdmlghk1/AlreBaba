package com.ssafy.alrebaba.channel.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ssafy.alrebaba.study.domain.Study;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelId;

    @ManyToOne(fetch = FetchType.LAZY) // cascade 옵션 제거
    @OnDelete(action = OnDeleteAction.CASCADE) // Study 삭제 시 DB 차원에서 채널 삭제 처리 (단, 채널 삭제 시 Study에는 영향 없음)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(name = "channel_name", nullable = false)
    private String channelName;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false)
    private ChannelType channelType;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp")
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Channel(Study study, String channelName, ChannelType channelType) {
        this.study = study;
        this.channelName = channelName;
        this.channelType = channelType;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
    }

}