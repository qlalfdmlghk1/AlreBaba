package com.ssafy.alrebaba.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberJoinRequest(

        @NotBlank(message = "이메일은 공백이 될 수 없습니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String username,

        @NotBlank(message = "비밀번호는 공백이 될 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()-+=<>?]).+$",
                message = "비밀번호는 최소 하나의 소문자, 숫자 및 특수문자를 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "닉네임은 공백이 될 수 없습니다.")
        @Pattern(
                regexp = "^[^@]+$",  // '@' 포함 불가능
                message = "닉네임에는 '@' 문자를 포함할 수 없습니다."
        )
        String nickname

) {}
