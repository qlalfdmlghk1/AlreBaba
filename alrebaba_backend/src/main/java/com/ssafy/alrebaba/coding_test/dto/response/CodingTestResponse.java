package com.ssafy.alrebaba.coding_test.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ssafy.alrebaba.problem.domain.Problem;
import lombok.Builder;

@Builder
public record CodingTestResponse(
	Long codingTestId,
	Long channelId,
	LocalDateTime startTime,
	LocalDateTime endTime
) {
}
