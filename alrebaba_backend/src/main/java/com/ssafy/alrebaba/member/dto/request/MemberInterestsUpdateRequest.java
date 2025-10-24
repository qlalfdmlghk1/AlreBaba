package com.ssafy.alrebaba.member.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MemberInterestsUpdateRequest(
        @NotNull(message = "관심사는 필수 항목입니다.")
        List<String> interests
) {}
