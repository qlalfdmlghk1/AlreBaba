package com.ssafy.alrebaba.event.domain;

import com.ssafy.alrebaba.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByStudyAndStartTimeBetween(Study study, LocalDateTime startTime, LocalDateTime endTime);

    // 아직 알림 전송되지 않은 이벤트 중, remindBeforeMinutes가 설정된 이벤트 조회
    List<Event> findByReminderSentFalseAndRemindBeforeMinutesIsNotNull();
}
