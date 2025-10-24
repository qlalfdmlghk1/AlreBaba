package com.ssafy.alrebaba.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberPasswordUpdateRequest(

        @NotBlank(message = "기존 비밀번호는 필수 항목입니다.") String oldPassword,
        @NotBlank(message = "새 비밀번호는 필수 항목입니다.") String newPassword

) {}

