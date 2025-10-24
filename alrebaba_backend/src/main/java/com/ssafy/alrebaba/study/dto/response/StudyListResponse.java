package com.ssafy.alrebaba.study.dto.response;

import lombok.Builder;

@Builder
public record StudyListResponse(
        Long studyId,
        String imageUrl
) {
}
