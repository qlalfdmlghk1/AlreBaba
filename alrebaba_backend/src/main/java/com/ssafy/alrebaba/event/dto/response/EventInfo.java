package com.ssafy.alrebaba.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.alrebaba.event.domain.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Builder
public record EventInfo(

        @Schema(description = "이벤트 ID", example = "1")
        Long eventId,

        @Schema(description = "이벤트 이름", example = "모의 코테")
        String eventName,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(description = "이벤트 시작 시간 (HH:mm 형식)", example = "14:00", type = "string")
        LocalTime startTime,

        @Schema(description = "이벤트 색상 코드", example = "#0Cd6Ef")
        String colorCode
) {

    public static EventInfo from(Event event) {
        return EventInfo.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .startTime(LocalTime.parse(event.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))) // 초 제거
                .colorCode(event.getColor())
                .build();
    }

}