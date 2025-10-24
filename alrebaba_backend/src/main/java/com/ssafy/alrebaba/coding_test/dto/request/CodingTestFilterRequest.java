package com.ssafy.alrebaba.coding_test.dto.request;

import java.time.LocalDateTime;


public record CodingTestFilterRequest(
	Long codingTestId,
	Long channelId,
	LocalDateTime startTime,
	LocalDateTime endTime
) {
}
