package com.ssafy.alrebaba.problem.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProblemUpdateRequest(
        @NotBlank(message = "문제 제목은 필수 입력 항목입니다.")
        String problemTitle,
        @NotBlank(message = "문제 제목은 필수 입력 항목입니다.")
        String problemUrl
) {
}
