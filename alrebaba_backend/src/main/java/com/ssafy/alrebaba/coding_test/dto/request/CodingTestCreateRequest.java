package com.ssafy.alrebaba.coding_test.dto.request;

import com.ssafy.alrebaba.problem.dto.request.ProblemCreateRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CodingTestCreateRequest(

        @NotNull(message = "채널 정보는 필수 입력 항목입니다.")
        Long channelId,

        @NotNull(message = "코딩테스트 시작 시간은 필수 입력 항목입니다.")
        LocalDateTime startTime,

        @NotNull(message = "코딩테스트 종료 시간은 필수 입력 항목입니다.")
        LocalDateTime endTime,

        @NotNull(message = "문제 리스트는 필수 입력 항목입니다.")
        @Size(min = 1, message = "적어도 하나의 문제를 포함해야 합니다.")
        List<ProblemCreateRequest> problemCreateRequestList

) {

}
