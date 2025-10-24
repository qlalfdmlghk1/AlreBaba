package com.ssafy.alrebaba.event.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record EventInfoListByDate(
        LocalDate date,              // 날짜
        List<EventInfo> events       // 해당 날짜의 이벤트 리스트
) {
    public static EventInfoListByDate of(LocalDate date, List<EventInfo> events) {
        return EventInfoListByDate.builder()
                .date(date)
                .events(events)
                .build();
    }
}
