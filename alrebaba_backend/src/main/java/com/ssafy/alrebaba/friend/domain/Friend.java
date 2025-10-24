package com.ssafy.alrebaba.friend.domain;

import com.ssafy.alrebaba.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "friends")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@IdClass(FriendId.class)
@EntityListeners(AuditingEntityListener.class)
public class Friend {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accept_id", nullable = false)
    private Member acceptMember;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Member requestMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
