package com.ssafy.alrebaba.member.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberAlarmUpdateRequest(
        @NotNull(message = "알림 설정 값은 필수 항목입니다.") Boolean isAlarmOn
) {}
