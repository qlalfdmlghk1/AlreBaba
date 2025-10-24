package com.ssafy.alrebaba.study.dto.response;

import com.ssafy.alrebaba.channel.dto.response.ChannelResponse;
import com.ssafy.alrebaba.member.dto.response.MemberInfoListByStatus;
import com.ssafy.alrebaba.study.domain.Study;
import lombok.Builder;

import java.util.List;

@Builder
public record StudyResponse(
        Long studyId,
        String studyName,
        String imageUrl,
        String description,
        Long createdBy,
        List<MemberInfoListByStatus> participants,
        List<ChannelResponse> channels
) {

    public static StudyResponse of(Study study, Long ownerId, List<MemberInfoListByStatus> participants, List<ChannelResponse> channels) {
        return StudyResponse.builder()
                .studyId(study.getStudyId())
                .studyName(study.getStudyName())
                .imageUrl(study.getImageUrl())
                .description(study.getDescription())
                .createdBy(ownerId)
                .participants(participants)
                .channels(channels)
                .build();
    }
}