package com.ssafy.alrebaba.interest.application;

import com.ssafy.alrebaba.interest.domain.InterestRepository;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final MemberRepository memberRepository;

    

}
