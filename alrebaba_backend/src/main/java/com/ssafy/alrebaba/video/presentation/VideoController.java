package com.ssafy.alrebaba.video.presentation;

import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.video.application.VideoService;
import com.ssafy.alrebaba.video.dto.response.VideoTokenResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.livekit.server.AccessToken;

@RestController
@RequiredArgsConstructor
public class VideoController {


    private final VideoService videoService;

    @PostMapping(value = "/studies/{study-id}/channel/{channel-id}/token")
    public ResponseEntity<VideoTokenResponse> createToken(@PathVariable("study-id") Long studyId,
                                                          @PathVariable("channel-id") Long channelId,
                                                          @AuthenticationPrincipal CustomMemberDetails loginMember) throws IllegalAccessException {
        AccessToken token = videoService.getAccessToken(studyId, channelId, loginMember);
        VideoTokenResponse videoTokenResponse = new VideoTokenResponse(token.toJwt(),
                loginMember.getMemberId());
        return ResponseEntity.ok(videoTokenResponse);

    }


    @PostMapping(value = "/livekit/webhook", consumes = "application/webhook+json")
    public ResponseEntity<String> receiveWebhook(@RequestHeader("Authorization") String authHeader, @RequestBody String body) {
        videoService.receiveWebhook(authHeader,body);
        return ResponseEntity.ok("ok");
    }

}