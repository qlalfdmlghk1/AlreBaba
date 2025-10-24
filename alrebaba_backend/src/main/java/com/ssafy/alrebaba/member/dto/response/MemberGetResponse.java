package com.ssafy.alrebaba.member.dto.response;

import com.ssafy.alrebaba.interest.domain.InterestName;
import com.ssafy.alrebaba.language.domain.LanguageName;
import com.ssafy.alrebaba.member.domain.Role;
import com.ssafy.alrebaba.member.domain.Status;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

// 회원조회API Response
@Builder
public record MemberGetResponse (

        Long memberId,
        String username,
        String nickname,
        String uniqueId,
        Role role,
        Status status,
        String profileImage,
        Boolean isAlarmOn,
        List<InterestName> interests,
        List<LanguageName> languages,
        LocalDateTime createdAt

){}
