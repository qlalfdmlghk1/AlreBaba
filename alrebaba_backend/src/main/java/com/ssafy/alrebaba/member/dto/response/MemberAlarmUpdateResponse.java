package com.ssafy.alrebaba.member.dto.response;

import lombok.Builder;

@Builder
public record MemberAlarmUpdateResponse(
        Long memberId,
        Boolean isAlarmOn
) {}
