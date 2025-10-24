package com.ssafy.alrebaba.member.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberInfoListByStatus(
        String status,
        List<MemberInfo> members
) {

    public static MemberInfoListByStatus of(String status, List<MemberInfo> memberInfoList) {
        return MemberInfoListByStatus.builder()
                .status(status)
                .members(memberInfoList)
                .build();
    }

}
