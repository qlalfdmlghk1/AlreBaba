package com.ssafy.alrebaba.video.application;

import com.ssafy.alrebaba.auth.exception.AuthException;
import com.ssafy.alrebaba.channel.domain.Channel;
import com.ssafy.alrebaba.channel.domain.ChannelRepository;
import com.ssafy.alrebaba.channel.domain.ChannelType;
import com.ssafy.alrebaba.common.exception.BadRequestException;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.study.domain.Study;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipant;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipantId;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipantRepository;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.WebhookReceiver;
import livekit.LivekitWebhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VideoService {

    @Value("${livekit.api.key}")
    private String LIVEKIT_API_KEY;

    @Value("${livekit.api.secret}")
    private String LIVEKIT_API_SECRET;

    private final StudyParticipantRepository studyParticipantRepository;
    private final ChannelRepository channelRepository;

    public AccessToken getAccessToken(Long studyId,
                                      Long channelId,
                                      CustomMemberDetails loginMember) throws IllegalAccessException {
        // 1. 스터디 참가 인원 확인
        StudyParticipant studyParticipant =
                studyParticipantRepository.findById(new StudyParticipantId(studyId, loginMember.getMemberId()))
                        .orElseThrow(()-> new IllegalAccessException("스터디 참가원이 아닙니다."));

        // 2. 채널 확인
        Channel channel = channelRepository.findById(channelId)
                .filter(c -> c.getChannelType() == ChannelType.MEETING)
                .orElseThrow(() -> new BadRequestException("잘못된 채널 타입이거나 존재하지 않는 채널입니다."));

        // 3. 채널이 스터디에 포함된 채널인지 확인
        if(!Objects.equals(channel.getStudy().getStudyId(), studyParticipant.getStudy().getStudyId())){
            throw new IllegalAccessException("스터디에 포함되지 않는 채널입니다.");
        }

        AccessToken token = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
        token.setName(loginMember.getNickname());
        token.setIdentity(loginMember.getUsername());

        //채널 이름을 사용한다면 중복이 가능하기 때문에
        token.addGrants(new RoomJoin(true), new RoomName(Long.toString(channelId)));
        return token;
    }

    public void receiveWebhook(String authHeader, String body){
        WebhookReceiver webhookReceiver = new WebhookReceiver(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
        try {
            LivekitWebhook.WebhookEvent event = webhookReceiver.receive(body, authHeader);
            System.out.println("LiveKit Webhook: " + event.toString());
        } catch (Exception e) {
            System.err.println("Error validating webhook event: " + e.getMessage());
        }
    }

}
