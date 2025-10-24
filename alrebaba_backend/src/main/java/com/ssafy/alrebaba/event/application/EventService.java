package com.ssafy.alrebaba.event.application;

import com.ssafy.alrebaba.common.exception.ForbiddenException;
import com.ssafy.alrebaba.common.exception.NotFoundException;
import com.ssafy.alrebaba.event.domain.Event;
import com.ssafy.alrebaba.event.domain.EventRepository;
import com.ssafy.alrebaba.event.dto.request.EventRequest;
import com.ssafy.alrebaba.event.dto.response.EventInfo;
import com.ssafy.alrebaba.event.dto.response.EventInfoListByDate;
import com.ssafy.alrebaba.event.dto.response.EventResponse;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.study.domain.Study;
import com.ssafy.alrebaba.study.domain.StudyRepository;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    /**
     * 스터디 일정 생성
     *
     * @param studyId 스터디 ID
     * @param loginId 일정 생성 회원 ID
     * @param request 일정 생성 요청 데이터
     */
    @Transactional
    public Long create(Long studyId, Long loginId, EventRequest request) {
        // 1. 스터디 유효성 검사
        Study study = validateStudy(studyId);

        // 2. 스터디 참가자 유효성 검사
        Member member = validateParticipant(studyId, loginId);

        // 3. 총 지속 시간 계산 (분 단위)
        int totalDurationMinutes = request.durationHours() * 60 + request.durationMinutes();

        // 5. 이벤트 생성
        Event event = Event.builder()
                .study(study)
                .createdBy(member)
                .eventName(request.eventName())
                .description(request.description())
                .startTime(request.startTime())
                .durationMinutes(totalDurationMinutes)
                .remindBeforeMinutes(request.remindBeforeMinutes())
                .color(request.color())
                .build();

        eventRepository.save(event);

        return event.getEventId();
    }

    /**
     * 연월별 스터디 일정 조회
     *
     * @param studyId   스터디 ID
     * @param yearMonth 조회할 연월
     * @return 일별 일정 리스트
     */
    @Transactional(readOnly = true)
    public List<EventInfoListByDate> getEventsByYearMonth(Long studyId, YearMonth yearMonth) {
        Study study = validateStudy(studyId);

        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Event> events = eventRepository.findAllByStudyAndStartTimeBetween(study, startOfMonth, endOfMonth);

        return events.stream()
                .collect(Collectors.groupingBy(event -> event.getStartTime().toLocalDate()))
                .entrySet().stream()
                .map(entry -> EventInfoListByDate.of(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(EventInfo::from)
                                .toList()
                ))
                .sorted(Comparator.comparing(EventInfoListByDate::date))
                .toList();
    }

    /**
     * 스터디 일정 상세 조회
     *
     * @param eventId 일정 ID
     */
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long studyId, Long eventId) {
        // 1. 스터디 유효성 검사
        validateStudy(studyId);

        // 2. 일정 유효성 검사
        Event event = validateEvent(eventId);

        return EventResponse.from(event);
    }

    /**
     * 스터디 일정 수정
     *
     * @param studyId 스터디 ID
     * @param loginId 로그인한 회원 ID
     * @param eventId 일정 ID
     * @param request 일정 수정 요청 DTO
     */
    @Transactional
    public void update(Long studyId, Long loginId, Long eventId, EventRequest request) {
        // 1. 스터디 유효성 검사
        validateStudy(studyId);

        // 2. 일정 유효성 검사
        Event event = validateEvent(eventId);

        // 3. 일정 생성자인지 확인
        if (!event.getCreatedBy().getMemberId().equals(loginId)) {
            throw new ForbiddenException("일정 수정 권한이 없습니다.");
        }

        // 4. 총 지속 시간 계산 (분 단위)
        int totalDurationMinutes = request.durationHours() * 60 + request.durationMinutes();

        // 5. 일정 업데이트
        event.update(
                request.eventName(),
                request.description(),
                request.startTime(),
                totalDurationMinutes,
                request.remindBeforeMinutes(),
                request.color()
        );
    }

    /**
     * 스터디 일정 삭제
     *
     * @param studyId 스터디 ID
     * @param loginId 로그인한 회원 ID
     * @param eventId 일정 ID
     */
    @Transactional
    public void delete(Long studyId, Long eventId, Long loginId) {
        // 1. 스터디 유효성 검사
        validateStudy(studyId);

        // 2. 일정 유효성 검사
        Event event = validateEvent(eventId);

        // 3. 일정 생성자인지 확인
        if (!event.getCreatedBy().getMemberId().equals(loginId)) {
            throw new ForbiddenException("일정 수정 권한이 없습니다.");
        }

        // 4. 일정 삭제
        eventRepository.delete(event);
    }

    private Study validateStudy(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundException("스터디가 존재하지 않습니다."));
    }

    private Member validateParticipant(Long studyId, Long memberId) {
        return studyParticipantRepository.findJoinedMemberByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new ForbiddenException("스터디 참여자만 이벤트를 생성할 수 있습니다."));
    }

    private Event validateEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("일정이 존재하지 않습니다."));
    }

}
