package com.ssafy.alrebaba.problem.dto.response;

import lombok.Builder;

@Builder
public record ProblemResponse(
	Long problemId,
	Long codingTestId,
	String problemTitle,
	String problemUrl
) {
}
