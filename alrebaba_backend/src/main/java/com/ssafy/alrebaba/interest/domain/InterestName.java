package com.ssafy.alrebaba.interest.domain;

import com.ssafy.alrebaba.common.util.AbstractCodedEnumConverter;
import com.ssafy.alrebaba.common.util.CodedEnum;
import com.ssafy.alrebaba.member.domain.Role;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.NoSuchElementException;


public enum InterestName implements CodedEnum<String> {

    ECOMMERCE_SHOPPING("이커머스"), // 이커머스 / 쇼핑몰
    FINANCE("금융"),            // 금융
    PORTAL_SOCIAL_MEDIA("소셜미디어"), // 포털 / 소셜미디어
    TELECOMMUNICATION_NETWORK("통신"), // 통신 / 네트워크
    EDUCATION("교육"),          // 교육
    PUBLIC_SECTOR("공공"),      // 공공
    MEDICAL_HEALTHCARE("의료/헬스케어"), // 의료 / 헬스케어
    MANUFACTURING("제조"),      // 제조
    HARDWARE_EMBEDDED("임베디드"),  // 임베디드
    GAME("게임"),               // 게임
    SECURITY_VACCINE("보안"),   // 보안 / 백신
    COMMUNITY("커뮤니티"),          // 커뮤니티
    COMMERCE("커머스"),           // 커머스
    ARTIFICIAL_INTELLIGENCE("인공지능"); // 인공지능

    private String key;

    InterestName(String key){
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @jakarta.persistence.Converter(autoApply = true)
    static class Converter extends AbstractCodedEnumConverter<InterestName, String> {
        public Converter() {
            super(InterestName.class);
        }
    }

}
