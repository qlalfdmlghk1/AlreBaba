package com.ssafy.alrebaba.channel.presentation;

import com.ssafy.alrebaba.channel.application.ChannelService;
import com.ssafy.alrebaba.channel.dto.request.ChannelCreateRequest;
import com.ssafy.alrebaba.channel.dto.request.ChannelNameUpdateRequest;
import com.ssafy.alrebaba.channel.dto.response.ChannelResponse;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/studies/{study-id}/channels")
@RequiredArgsConstructor
@Tag(name = "Channel", description = "채널 관리 API")
public class ChannelController {

    private final ChannelService channelService;

    // 스터디 내 채널 생성
    @PostMapping
    @Operation(
            summary = "스터디 내 채널 생성",
            description = "스터디 내 새로운 채널 생성 성공",
            responses = {
                    @ApiResponse(responseCode = "201", description = "채널 생성 성공",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    public ResponseEntity<Map<String, Object>> createChannel(
            @PathVariable("study-id") Long studyId,
            @Valid @RequestBody ChannelCreateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        Long channelId = channelService.create(studyId, loginId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/studies/{study-id}/channels/{channel-id}")
                .buildAndExpand(studyId, channelId)
                .toUri();
        Map<String, Object> response = Map.of("channelId", channelId);
        return ResponseEntity.created(uri).body(response);
    }

    // 스터디 내 채널 목록 조회
    @GetMapping
    @Operation(
            summary = "스터디 채널 목록 조회",
            description = "스터디 내의 모든 채널의 목록 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "채널 목록 조회 성공",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelResponse.class))))
            }
    )
    public ResponseEntity<List<ChannelResponse>> getChannels(
            @PathVariable("study-id") Long studyId
    ) {
        List<ChannelResponse> responses = channelService.findAllByStudy(studyId);
        return ResponseEntity.ok(responses);
    }

    // 스터디 내 채널 이름 변경
    @PatchMapping("/{channel-id}")
    @Operation(
            summary = "스터디 내 채널 이름 변경",
            description = "채널의 이름을 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "채널 이름 변경 성공")
            }
    )
    public ResponseEntity<Void> updateChannelName(
            @PathVariable("study-id") Long studyId,
            @PathVariable("channel-id") Long channelId,
            @Valid @RequestBody ChannelNameUpdateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        channelService.updateName(studyId, channelId, loginId, request);
        return ResponseEntity.noContent().build();
    }

    // 스터디 채널 삭제
    @DeleteMapping("/{channel-id}")
    @Operation(
            summary = "스터디 내 채널 삭제",
            description = "채널을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "채널 삭제 성공")
            }
    )
    public ResponseEntity<Void> deleteChannel(
            @PathVariable("study-id") Long studyId,
            @PathVariable("channel-id") Long channelId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        channelService.delete(studyId, channelId, loginId);
        return ResponseEntity.noContent().build();
    }

}
