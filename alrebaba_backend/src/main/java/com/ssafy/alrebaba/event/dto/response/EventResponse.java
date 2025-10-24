package com.ssafy.alrebaba.event.dto.response;

import com.ssafy.alrebaba.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Builder
public record EventResponse(
    Long eventId,
    Long studyId,
    Long createdBy,
    String eventName,
    String description,
    LocalDate eventDate,
    LocalTime startTime,
    int durationHours,
    int durationMinutes,
    Integer remindBeforeMinutes,
    String colorCode

) {

    public static EventResponse from(Event event) {
        final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
        int durationHours = event.getDurationMinutes() / 60;
        int durationMinutes = event.getDurationMinutes() % 60;
        return EventResponse.builder()
                .eventId(event.getEventId())
                .studyId(event.getStudy().getStudyId())
                .createdBy(event.getCreatedBy().getMemberId())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .eventDate(event.getStartTime().toLocalDate())
                .startTime(LocalTime.parse(event.getStartTime().toLocalTime().format(TIME_FORMATTER))) // 초 제거
                .durationHours(durationHours)
                .durationMinutes(durationMinutes)
                .remindBeforeMinutes(event.getRemindBeforeMinutes())
                .colorCode(event.getColor())
                .build();
    }

}
