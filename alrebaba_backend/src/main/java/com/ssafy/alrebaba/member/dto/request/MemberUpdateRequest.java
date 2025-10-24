package com.ssafy.alrebaba.member.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberUpdateRequest(

        String nickname,         // 닉네임
        List<String> interests,  // 관심사
        List<String> languages   // 선호 언어

) {}
