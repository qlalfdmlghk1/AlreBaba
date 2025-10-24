package com.ssafy.alrebaba.member.dto.response;

import com.ssafy.alrebaba.member.domain.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

@Builder
public record MemberStatusUpdateResponse(

        Long memberId,
        @Enumerated(EnumType.STRING)
        Status status

) {}
