package com.ssafy.alrebaba.study.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StudyUpdateRequest(

        @Size(min = 2, max = 15, message = "스터디 이름은 2자 이상 15자 이하로 입력해주세요.")
        String studyName,

        String description

) {
}
