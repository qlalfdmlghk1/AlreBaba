package com.ssafy.alrebaba.coding_test.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 코딩테스트와 문제들이 함께 생성된 결과를 응답할 DTO
 */
@Builder
public record CombinedCodingTestResponse(
        Long codingTestId,
        Long channelId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<Long> problemIds
) {
}
