package com.ssafy.alrebaba.member.dto.request;

import com.ssafy.alrebaba.member.domain.Status;
import jakarta.validation.constraints.NotNull;

public record MemberStatusUpdateRequest(

        @NotNull(message = "상태 값은 필수 항목입니다.") Status status

) {}

