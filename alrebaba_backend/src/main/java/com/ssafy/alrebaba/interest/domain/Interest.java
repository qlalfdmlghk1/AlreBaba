package com.ssafy.alrebaba.interest.domain;

import com.ssafy.alrebaba.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long InterestId;

    @Enumerated(EnumType.STRING)
    private InterestName interestName;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 명시적 생성자 추가
    public Interest(InterestName interestName, Member member) {
        this.interestName = interestName;
        this.member = member;

    }

}
