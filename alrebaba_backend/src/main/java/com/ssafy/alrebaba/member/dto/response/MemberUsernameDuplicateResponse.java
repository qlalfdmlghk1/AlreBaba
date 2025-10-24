package com.ssafy.alrebaba.member.dto.response;

import lombok.Builder;

// 이메일 중복확인시 Response
@Builder
public record MemberUsernameDuplicateResponse(

        Boolean isDuplicated

) {}
