package com.ssafy.alrebaba.member.dto.response;

import com.ssafy.alrebaba.member.domain.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

import java.time.LocalDateTime;

// 회원가입시 Response
@Builder
public record MemberJoinResponse(

        Long memberId,
        String username,
        String nickname,
        String uniqueId,
        @Enumerated(EnumType.STRING)
        Status status,
        String profileImage,
        LocalDateTime createdAt

) {}