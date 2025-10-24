package com.ssafy.alrebaba.friend.presentation;

import com.ssafy.alrebaba.friend.application.FriendService;
import com.ssafy.alrebaba.friend.dto.request.FriendRequestDto;
import com.ssafy.alrebaba.friend.dto.request.FriendSearchRequestDto;
import com.ssafy.alrebaba.friend.dto.request.FriendStatusUpdateRequestDto;
import com.ssafy.alrebaba.friend.dto.request.SentFriendRequestsRequestDto;
import com.ssafy.alrebaba.friend.dto.response.FriendListResponseDto;
import com.ssafy.alrebaba.friend.dto.response.FriendSearchResponseDto;
import com.ssafy.alrebaba.friend.dto.response.SentFriendRequestsResponseDto;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Friend", description = "친구 관리 API")
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 요청 보내기", description = "다른 회원에게 친구 요청을 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 성공"),
            @ApiResponse(responseCode = "400", description = "이미 친구이거나 요청이 존재함"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @RequestBody FriendRequestDto requestDto) {
        // record의 경우 accessor는 requestDto.acceptId(),
        // CustomMemberDetails는 클래스이므로 getMemberId() 사용
        return ResponseEntity.ok(friendService.sendFriendRequest(loginMember.getMemberId(), requestDto));
    }

    @Operation(summary = "친구 목록 조회", description = "회원의 친구 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<?> getFriendList(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @PathVariable Long memberId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "30") Integer pageSize) {
        // 회원 아이디 일치는 서비스에서 검증하도록 함
        return ResponseEntity.ok(friendService.getFriendList(memberId, lastId, pageSize));
    }

    @Operation(summary = "차단된 친구 목록 조회", description = "내가 차단한 친구들의 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "차단된 친구 목록 조회 성공")
    })
    @GetMapping("/ban")
    public ResponseEntity<?> getBlockedFriendList(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "30") Integer pageSize) {

        Long memberId = loginMember.getMemberId();
        return ResponseEntity.ok(friendService.getBlockedFriendList(memberId, lastId, pageSize));
    }

    @Operation(summary = "받은 친구 요청 목록 조회", description = "회원이 받은 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "받은 친구 요청 조회 성공")
    })
    @GetMapping("/requests-received")
    public ResponseEntity<?> getReceivedFriendRequests(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(name = "lastId", required = false) Long lastId) {
        FriendListResponseDto response = friendService.getReceivedFriendRequests(loginMember.getMemberId(), lastId, pageSize);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보낸 친구 요청 목록 조회", description = "회원이 보낸 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보낸 친구 요청 조회 성공")
    })
    @GetMapping("/requests-sent")
    public ResponseEntity<?> getSentFriendRequests(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @Valid SentFriendRequestsRequestDto requestDto) {
        // record DTO는 immutable하므로 새 인스턴스를 생성하여 회원 ID를 주입
        SentFriendRequestsRequestDto newRequestDto =
                new SentFriendRequestsRequestDto(loginMember.getMemberId(), requestDto.lastId(), requestDto.pageSize());
        SentFriendRequestsResponseDto response = friendService.getSentFriendRequestsNoOffset(
                newRequestDto.memberId(), newRequestDto.lastId(), newRequestDto.pageSize());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 검색", description = "회원 닉네임을 검색하여 친구를 찾습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchFriends(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @Valid FriendSearchRequestDto requestDto) {
        // 현재 로그인 회원의 memberId를 서비스에 전달하여 검색 시 차단된 친구를 필터링
        FriendSearchResponseDto response = friendService.searchFriends(loginMember.getMemberId(), requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    @Operation(
            summary = "친구 상태 변경",
            description = "친구 요청 수락, 차단 등의 상태 변경을 수행합니다. -> FOLLOWING, REQUESTED, BANNED",
            responses = {
                    @ApiResponse(responseCode = "200", description = "상태 변경 성공")
            }
    )
    public ResponseEntity<?> updateFriendStatus(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @RequestBody FriendStatusUpdateRequestDto requestDto,
            @Parameter(name = "notificationId", description = "알림에서 처리된 경우 해당 알림의 ID", required = false)
            @RequestParam(value = "notificationId", required = false) Long notificationId) {
        log.info("notificationId: {}", notificationId);
        return ResponseEntity.ok(friendService.updateFriendStatus(loginMember.getMemberId(), requestDto, notificationId));
    }



    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "친구 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "자기 자신과의 친구 관계 삭제 불가")
    })
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> deleteFriend(
            @AuthenticationPrincipal CustomMemberDetails loginMember,
            @PathVariable Long friendId) {
        if (loginMember.getMemberId().equals(friendId)) {
            return ResponseEntity.badRequest().body("자기 자신과의 친구 관계는 삭제할 수 없습니다.");
        }
        friendService.deleteFriend(loginMember.getMemberId(), friendId);
        return ResponseEntity.noContent().build();
    }
}
