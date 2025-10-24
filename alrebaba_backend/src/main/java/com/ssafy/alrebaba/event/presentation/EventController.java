package com.ssafy.alrebaba.event.presentation;

import com.ssafy.alrebaba.event.application.EventService;
import com.ssafy.alrebaba.event.dto.request.EventRequest;
import com.ssafy.alrebaba.event.dto.response.EventInfoListByDate;
import com.ssafy.alrebaba.event.dto.response.EventResponse;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/studies/{study-id}/events")
@RequiredArgsConstructor
@Tag(name = "Event", description = "일정 관리 API")
public class EventController {

    private final EventService eventService;

    // 스터디 일정 생성
    @PostMapping
    @Operation(
            summary = "스터디 내 일정 생성",
            description = "스터디 내 새로운 일정 생성 성공",
            responses = {
                    @ApiResponse(responseCode = "201", description = "일정 생성 성공",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    public ResponseEntity<Map<String, Object>> createEvent(
            @PathVariable("study-id") Long studyId,
            @Valid @RequestBody EventRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        Long eventId = eventService.create(studyId, loginId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/studies/{study-id}/events/{event-id}")
                .buildAndExpand(studyId, eventId)
                .toUri();
        Map<String,Object> response = Map.of("eventId", eventId);
        return ResponseEntity.created(uri).body(response);
    }

    // 연월별 스터디 일정 리스트 조회 (일별 그룹화)
    @GetMapping
    @Operation(
            summary = "연월별 스터디 일정 조회",
            description = "특정 연월의 스터디 일정을 조회하여 일별로 그룹화",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = EventInfoListByDate.class))),
            }
    )
    public ResponseEntity<List<EventInfoListByDate>> getEventsByYearMonth(
            @PathVariable("study-id") Long studyId,
            @Parameter(description = "조회할 연월 (yyyy-MM)", required = true, example = "2025-02")
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        List<EventInfoListByDate> response = eventService.getEventsByYearMonth(studyId, yearMonth);
        return ResponseEntity.ok(response);
    }

    // 스터디 일정 상세 조회
    @GetMapping("/{event-id}")
    @Operation(
            summary = "스터디 일정 상세 조회",
            description = "특정 스터디 일정의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = EventResponse.class)))
            }
    )
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable("study-id") Long studyId,
            @PathVariable("event-id") Long eventId) {
        EventResponse response = eventService.getEvent(studyId, eventId);
        return ResponseEntity.ok(response);
    }

    // 스터디 일정 수정
    @PatchMapping("/{event-id}")
    @Operation(
            summary = "스터디 일정 수정",
            description =  "특정 스터디의 일정을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "일정 수정 성공")
            }
    )
    public ResponseEntity<Void> updateEvent(
            @PathVariable("study-id") Long studyId,
            @PathVariable("event-id") Long eventId,
            @Valid @RequestBody EventRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        eventService.update(studyId, loginId, eventId, request);
        return ResponseEntity.noContent().build();
    }

    // 스터디 삭제
    @DeleteMapping("/{event-id}")
    @Operation(
            summary = "스터디 내 일정 삭제",
            description = "일정을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "일정 삭제 성공")
            }
    )
    public ResponseEntity<Void> deleteEvent(
            @PathVariable("study-id") Long studyId,
            @PathVariable("event-id") Long eventId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        eventService.delete(studyId, eventId, loginId);
        return ResponseEntity.noContent().build();
    }

}
