package com.ssafy.alrebaba.problem.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProblemCreateRequest(
		@NotBlank(message = "문제 title은 필수 입력항목입니다.")
		String problemTitle,

		@NotBlank(message = "문제 URL은 필수 입력 항목입니다.")
		String problemUrl
) {
}
