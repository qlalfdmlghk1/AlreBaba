package com.ssafy.alrebaba.channel.application;

import com.ssafy.alrebaba.channel.domain.Channel;
import com.ssafy.alrebaba.channel.domain.ChannelRepository;
import com.ssafy.alrebaba.channel.domain.ChannelType;
import com.ssafy.alrebaba.channel.dto.request.ChannelCreateRequest;
import com.ssafy.alrebaba.channel.dto.request.ChannelNameUpdateRequest;
import com.ssafy.alrebaba.channel.dto.response.ChannelResponse;
import com.ssafy.alrebaba.common.exception.ForbiddenException;
import com.ssafy.alrebaba.common.exception.NotFoundException;
import com.ssafy.alrebaba.study.domain.Study;
import com.ssafy.alrebaba.study.domain.StudyRepository;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final StudyRepository studyRepository;
    private final StudyParticipantRepository studyParticipantRepository;

    /**
     * 스터디 내 채널 생성
     *
     * @param studyId 스터디 ID
     * @param memberId 로그인 ID
     * @param request 채널 생성 요청 데이터
     */
    @Transactional
    public Long create(Long studyId, Long memberId, ChannelCreateRequest request) {
        // 1. 스터디 유효성 검사
        Study study = validateStudy(studyId);

        // 2. 스터디 채널 생성자가 스터디 참여자인지 확인
        validateParticipant(studyId, memberId);

        // 3. 문자열 타입을 ChannelType Enum으로 매핑
        ChannelType channelType = ChannelType.fromString(request.channelType());

        Channel channel = Channel.builder()
                .study(study)
                .channelName(request.channelName())
                .channelType(channelType)
                .build();

        channelRepository.save(channel);

        return channel.getChannelId();
    }

    /**
     * 스터디 내 채널 목록 조회
     *
     * @param studyId 스터디 ID
     */
    @Transactional(readOnly = true)
    public List<ChannelResponse> findAllByStudy(Long studyId) {
        // 1. 스터디 유효성 검사
        Study study = validateStudy(studyId);

        List<Channel> channels = channelRepository.findAllByStudy(study);

        return channels.stream()
                .map(ChannelResponse::from)
                .toList();
    }

    /**
     * 스터디 내 채널 이름 변경
     *
     * @param studyId 스터디 ID
     * @param channelId 채널 ID
     * @param memberId 로그인 ID
     * @param request 채널 이름 수정 요청 데이터
     */
    @Transactional
    public void updateName(Long studyId, Long channelId, Long memberId, ChannelNameUpdateRequest request) {
        // 1. 스터디 유효성 검사
        validateStudy(studyId);

        // 2. 스터디 채널 생성자가 스터디 참여자인지 확인
        validateParticipant(studyId, memberId);

        // 3. 채널 유효성 검사
        Channel channel = validateChannel(channelId);

        // 4. 채널 이름 변경
        channel.updateChannelName(request.channelName());
    }

    /**
     * 채널 삭제
     *
     * @param studyId 스터디 ID
     * @param channelId 채널 ID
     * @param memberId 로그인 ID
     */
    @Transactional
    public void delete(Long studyId, Long channelId, Long memberId) {
        // 1. 스터디 유효성 검사
        validateStudy(studyId);

        // 2. 스터디 채널 생성자가 스터디 참여자인지 확인
        validateParticipant(studyId, memberId);

        // 3. 채널 유효성 검사
        Channel channel = validateChannel(channelId);

        channelRepository.delete(channel);
    }

    private Study validateStudy(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundException("스터디가 존재하지 않습니다."));
    }

    private void validateParticipant(Long studyId, Long memberId) {
        if (!studyParticipantRepository.existsByStudyIdAndMemberIdJoin(studyId, memberId)) {
            throw new ForbiddenException("스터디 참여자만 채널을 처리(생성, 수정, 삭제)할 수 있습니다.");
        }
    }

    private Channel validateChannel(Long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("채널이 존재하지 않습니다."));
    }

}
