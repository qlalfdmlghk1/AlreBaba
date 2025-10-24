package com.ssafy.alrebaba.auth.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    private Long memberId;
    private String role;
    private String name;
    private String username;
}
