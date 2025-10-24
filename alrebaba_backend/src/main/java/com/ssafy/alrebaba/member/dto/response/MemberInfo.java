package com.ssafy.alrebaba.member.dto.response;

import com.ssafy.alrebaba.common.storage.application.ImageUtil;
import com.ssafy.alrebaba.member.domain.Member;
import lombok.Builder;

@Builder
public record MemberInfo(
        Long memberId,
        String nickname,
        String username,
        String profileImage,
        String status
){

    public static MemberInfo of(Member member, ImageUtil imageUtil) {
        String profileImage = imageUtil.getPreSignedUrl(member.getProfileImage());

        return MemberInfo.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profileImage(profileImage)
                .username(member.getUsername())
                .status(member.getStatus().name())
                .build();
    }

}