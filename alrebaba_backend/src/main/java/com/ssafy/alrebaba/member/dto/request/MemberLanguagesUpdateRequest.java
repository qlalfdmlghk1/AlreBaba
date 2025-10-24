package com.ssafy.alrebaba.member.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MemberLanguagesUpdateRequest(
        @NotNull(message = "선호 언어는 필수 항목입니다.")
        List<String> languages
) {}
