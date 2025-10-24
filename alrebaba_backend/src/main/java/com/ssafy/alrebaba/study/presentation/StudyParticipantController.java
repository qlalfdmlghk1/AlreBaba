package com.ssafy.alrebaba.study.presentation;

import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.member.dto.response.MemberInfoListByStatus;
import com.ssafy.alrebaba.study.application.StudyParticipantService;
import com.ssafy.alrebaba.study.dto.response.ParticipantResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies/{study-id}/members")
@RequiredArgsConstructor
@Tag(name = "Study Participant", description = "스터디 참가자 관리 API")
public class StudyParticipantController {

    private final StudyParticipantService studyParticipantService;

    // 스터디 참가자 목록 조회
    @GetMapping
    @Operation(
            summary = "스터디 참가자 목록 조회",
            responses = @ApiResponse(responseCode = "200", description = "스터디 참가자 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = List.class)))
    )
    public ResponseEntity<List<MemberInfoListByStatus>> getStudyMembers(@PathVariable("study-id") Long studyId) {
        List<MemberInfoListByStatus> response = studyParticipantService.getParticipants(studyId);
        return ResponseEntity.ok(response);
    }

    // 스터디 참가자 정보 조회
    @GetMapping("/role")
    @Operation(
            summary = "스터디 참가자 정보(ROLE) 조회",
            responses = @ApiResponse(responseCode = "200", description = "스터디 참가자 목록 조회 성공")
    )
    public ResponseEntity<ParticipantResponse> getStudyMember(
            @PathVariable("study-id") Long studyId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        ParticipantResponse response = studyParticipantService.getParticipantInfo(studyId, loginId);
        return ResponseEntity.ok(response);
    }



    // 스터디 초대 요청
    // TODO: 우선 {invitee-id}를 PathVariable로 개발했는데, RequestParam으로 하는 것이 좋을까?
    @PostMapping("/{invitee-id}")
    @Operation(
            summary = "스터디 초대 요청",
            responses = @ApiResponse(responseCode = "200", description = "스터디 초대 요청 보내기")
    )
    public ResponseEntity<Void> inviteParticipant(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @PathVariable("study-id") Long studyId,
            @PathVariable("invitee-id") Long inviteeId) {
        Long inviterId = loginMember.getMemberId();
        studyParticipantService.registerParticipant(studyId, inviteeId, inviterId);
        return ResponseEntity.ok().build();
    }

    // 스터디 초대 수락
    @PatchMapping("/accept")
    @Operation(
            summary = "스터디 초대 요청 수락",
            responses = @ApiResponse(responseCode = "200", description = "스터디 초대 요청 수락")
    )
    public ResponseEntity<Void> acceptInvitation(
            @PathVariable("study-id") Long studyId,
            @RequestParam("notification-id") Long notificationId,
            @AuthenticationPrincipal CustomMemberDetails loginMember) {
        Long inviteeId = loginMember.getMemberId();
        studyParticipantService.acceptInvitation(studyId, inviteeId, notificationId);
        return ResponseEntity.noContent().build();
    }

    // 스터디 초대 거절
    @DeleteMapping("/reject")
    @Operation(
            summary = "스터디 초대 요청 거절",
            responses = @ApiResponse(responseCode = "204", description = "스터디 초대 거절 성공")
    )
    public ResponseEntity<Void> rejectInvitation(
            @PathVariable("study-id") Long studyId,
            @RequestParam("notification-id") Long notificationId,
            @AuthenticationPrincipal CustomMemberDetails loginMember) {
        Long inviteeId = loginMember.getMemberId();
        studyParticipantService.rejectInvitation(studyId, inviteeId, notificationId);
        return ResponseEntity.noContent().build();
    }

    // 스터디 나가기 또는 참가자 내보내기
    @DeleteMapping("/{target-member-id}")
    @Operation(
            summary = "스터다 나가기 또는 참가자 내보내기",
            responses = {
                    @ApiResponse(responseCode = "204", description = "스터디 나가기 또는 내보내기 성공")
            }
    )
    public ResponseEntity<Void> exitOrRemoveParticipant(
            @PathVariable("study-id") Long studyId,
            @Parameter(description = "대상 참가자의 ID (본인이 직접 나가거나, 호스트가 특정 참가자를 내보냄)", example = "2")
            @PathVariable("target-member-id") Long targetId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        studyParticipantService.exitStudy(studyId, targetId, loginId);
        return ResponseEntity.noContent().build();
    }

}