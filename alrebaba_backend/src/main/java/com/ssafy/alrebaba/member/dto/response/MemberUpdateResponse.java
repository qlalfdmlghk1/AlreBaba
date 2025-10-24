package com.ssafy.alrebaba.member.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

// 회원정보수정시 Response
@Builder
public record MemberUpdateResponse(

        Long memberId,
        String username,
        String nickname,
        String profileImage,
        String status,
        String uniqueId,
        LocalDateTime updatedAt,
        Boolean alarmOn,
        List<String> interests, // 단순한 문자열 배열
        List<String> languages // 단순한 문자열 배열

) {}

