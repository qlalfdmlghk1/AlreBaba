package com.ssafy.alrebaba.language.application;

import com.ssafy.alrebaba.language.domain.LanguageRepository;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final MemberRepository memberRepository;
}
