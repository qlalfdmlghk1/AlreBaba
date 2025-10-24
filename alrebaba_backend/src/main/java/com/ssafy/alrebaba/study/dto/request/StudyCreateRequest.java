package com.ssafy.alrebaba.study.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudyCreateRequest(

        @NotBlank(message = "스터디 이름은 필수 입력 항목입니다.")
        @Size(min = 2, max = 15, message = "스터디 이름은 2자 이상 15자 이하로 입력해주세요.")
        String studyName

        // TODO: 이미지는 추후 추가
) {
}
