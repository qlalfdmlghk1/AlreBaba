package com.ssafy.alrebaba.event.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Schema(description = "이벤트 생성 및 수정 요청 DTO")
public record EventRequest(

        @NotBlank(message = "이벤트 이름은 필수 입력 항목입니다.")
        @Size(min = 2, max = 15, message = "이벤트 이름은 2자 이상 15자 이하로 입력해주세요.")
        @Schema(description = "이벤트 이름", example = "모의 코테")
        String eventName,

        @Size(max = 100, message = "이벤트 설명은 100자 이하로 입력해주세요.")
        @Schema(description = "이벤트 설명 (선택 사항)", example = "늦지 말고 참석하세요~")
        String description,

        @NotNull(message = "이벤트 시작 시간은 필수 입력 항목입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        @FutureOrPresent(message = "이벤트 시작 시간은 현재 시간 이후여야 합니다.")
        @Schema(description = "이벤트 시작 시간 (yyyy-MM-dd HH:mm 형식)", example = "2025-02-03 14:00", type = "string")
        LocalDateTime startTime,

        @PositiveOrZero(message = "지속 시간(시간)은 0 이상이어야 합니다.")
        @Max(value = 12, message = "지속 시간(시간)은 최대 12시간까지 설정할 수 있습니다.")
        @Schema(description = "이벤트 지속 시간 (시간)", example = "2")
        int durationHours,

        @PositiveOrZero(message = "지속 시간(분)은 0 이상이어야 합니다.")
        @Max(value = 59, message = "지속 시간(분)은 최대 59분까지 설정할 수 있습니다.")
        @Schema(description = "이벤트 지속 시간 (분)", example = "0")
        int durationMinutes,

        @Min(value = 1, message = "알림 시간은 최소 1분 전이어야 합니다.")
        @Max(value = 99, message = "알림 시간은 최대 99분 전까지 설정할 수 있습니다.")
        @Schema(description = "알림 시간 (분)", example = "10")
        Integer remindBeforeMinutes,

        @NotBlank(message = "이벤트 색상은 필수 선택 항목입니다.")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "컬러 코드는 '#'로 시작하는 7자리 16진수여야 합니다.")
        @Schema(description = "이벤트 색상 (HEX 코드)", example = "#0Cd6Ef")
        String color

) {
}
