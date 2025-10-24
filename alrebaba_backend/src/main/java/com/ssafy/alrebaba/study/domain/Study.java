package com.ssafy.alrebaba.study.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "studies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long studyId;

    @Column(name = "study_name", nullable = false)
    private String studyName;

    @Column(columnDefinition = "text")
    private String description;     // Home 채널 - markdown 저장

    @Setter
    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp")
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Study(String studyName, String imageUrl) {
        this.studyName = studyName;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public void updateStudyName(String studyName) {
        this.studyName = studyName;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

}
