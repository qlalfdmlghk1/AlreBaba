package com.ssafy.alrebaba.member.presentation;

import com.ssafy.alrebaba.member.application.MemberService;
import com.ssafy.alrebaba.member.dto.request.*;
import com.ssafy.alrebaba.member.dto.response.MemberAlarmUpdateResponse;
import com.ssafy.alrebaba.member.dto.response.MemberJoinResponse;
import com.ssafy.alrebaba.member.dto.response.MemberStatusUpdateResponse;
import com.ssafy.alrebaba.member.dto.response.MemberUsernameDuplicateResponse;
import com.ssafy.alrebaba.member.exception.MemberException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원관리 API")
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 OK!!"),
            @ApiResponse(responseCode = "400", description = "이메일/닉네임 중복 등"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping(value = "/signup")
    public ResponseEntity<MemberJoinResponse> joinMember(@Valid @RequestBody MemberJoinRequest memberJoinRequest) {
        MemberJoinResponse response = memberService.createMemberService(memberJoinRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 이메일 중복확인
    @PostMapping("/check-email")
    @Operation(summary = "이메일 중복 확인", description = "입력된 이메일의 중복 여부를 확인합니다.")
    public ResponseEntity<MemberUsernameDuplicateResponse> checkEmail(
            @RequestBody MemberUsernameDuplicateRequest memberUsernameDuplicateRequest) {
        MemberUsernameDuplicateResponse response =
                memberService.duplicateUsernameService(memberUsernameDuplicateRequest.username());
        return ResponseEntity.ok(response);
    }

    // 로그인한 본인 회원 정보 조회
    @GetMapping
    @Operation(summary = "로그인 본인 회원 정보 확인", description = "로그인한 본인 회원 정보를 확인합니다.")
    public ResponseEntity<?> getMemberController(@AuthenticationPrincipal CustomMemberDetails loginMember) {
        try {
            return ResponseEntity.ok(memberService.getMemberService(loginMember));
        } catch (MemberException.MemberBadRequestException e) {
            log.error("회원 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 회원 정보 조회 (member-id)
    @GetMapping("/{member-id}")
    @Operation(summary = "회원 정보 확인", description = "회원 정보를 확인합니다.")
    public ResponseEntity<?> getMemberController(
            @PathVariable(name = "member-id") Long memberId,
            @AuthenticationPrincipal CustomMemberDetails loginMember) {
        try {
            return ResponseEntity.ok(memberService.getMemberService(memberId));
        } catch (MemberException.MemberBadRequestException e) {
            log.error("회원 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // (기존) 회원 정보 수정 API (닉네임, 관심사, 선호 언어 함께 수정)
    @PatchMapping(value = "/update-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원 정보 수정", description = "프로필 이미지를 제외한 정보를 수정합니다.")
    public ResponseEntity<?> updateMember(
            @Valid @RequestPart("member") MemberUpdateRequest memberUpdateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        try {
            var response = memberService.updateMember(memberUpdateRequest, loginMember);
            return ResponseEntity.ok(response);
        } catch (MemberException.MemberBadRequestException e) {
            log.error("회원 정보 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 별도 닉네임 수정 API
    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 변경", description = "회원의 닉네임을 변경합니다.")
    public ResponseEntity<?> updateNickname(
            @Valid @RequestBody MemberNicknameUpdateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        try {
            var response = memberService.updateNickname(request, loginMember);
            return ResponseEntity.ok(response);
        } catch (MemberException.MemberBadRequestException e) {
            log.error("닉네임 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 별도 관심사 수정 API
    @PatchMapping("/interests")
    @Operation(summary = "관심사 변경", description = "회원의 관심사를 변경합니다.")
    public ResponseEntity<?> updateInterests(
            @Valid @RequestBody MemberInterestsUpdateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        try {
            var response = memberService.updateInterests(request, loginMember);
            return ResponseEntity.ok(response);
        } catch (MemberException.MemberBadRequestException e) {
            log.error("관심사 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 별도 선호 언어 수정 API
    @PatchMapping("/languages")
    @Operation(summary = "선호 언어 변경", description = "회원의 선호 언어를 변경합니다.")
    public ResponseEntity<?> updateLanguages(
            @Valid @RequestBody MemberLanguagesUpdateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        try {
            var response = memberService.updateLanguages(request, loginMember);
            return ResponseEntity.ok(response);
        } catch (MemberException.MemberBadRequestException e) {
            log.error("선호 언어 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 프로필 이미지 수정 (Multipart 요청)
    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 수정", description = "회원의 프로필 이미지를 수정합니다.")
    public ResponseEntity<?> updateProfileImage(
            @RequestPart("image") MultipartFile multipartFile,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        try {
            String imageUrl = memberService.updateProfileImage(loginMember.getMemberId(), multipartFile, loginMember);
            return ResponseEntity.ok(Map.of("profileImage", imageUrl));
        } catch (Exception e) {
            log.error("프로필 이미지 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 비밀번호 수정
    @PatchMapping("/password")
    @Operation(summary = "비밀번호 수정", description = "사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody MemberPasswordUpdateRequest passwordUpdateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        try {
            memberService.updatePassword(passwordUpdateRequest, loginMember);
            return ResponseEntity.noContent().build();
        } catch (MemberException.MemberBadRequestException e) {
            log.error("비밀번호 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 상태 변경
    @PatchMapping("/status")
    @Operation(summary = "상태 변경", description = "회원의 상태를 변경합니다.")
    public ResponseEntity<?> updateStatus(
            @Valid @RequestBody MemberStatusUpdateRequest statusUpdateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        try {
            MemberStatusUpdateResponse response = memberService.updateStatus(statusUpdateRequest, loginMember);
            return ResponseEntity.ok(response);
        } catch (MemberException.MemberBadRequestException e) {
            log.error("상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 회원 알림 설정
    @PatchMapping("/alarm")
    @Operation(summary = "알림 설정 변경", description = "회원의 알림 설정을 변경합니다.")
    public ResponseEntity<?> updateAlarmStatus(
            @Valid @RequestBody MemberAlarmUpdateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        try {
            MemberAlarmUpdateResponse response = memberService.updateAlarmStatus(request, loginMember);
            return ResponseEntity.ok(response);
        } catch (MemberException.MemberBadRequestException e) {
            log.error("알림 설정 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 회원 삭제
    @DeleteMapping("")
    public ResponseEntity<?> removeMember(@AuthenticationPrincipal CustomMemberDetails loginMember) {
        try {
            memberService.deleteMember(loginMember);
            return ResponseEntity.noContent().build();
        } catch (MemberException.MemberBadRequestException e) {
            log.error("회원 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
