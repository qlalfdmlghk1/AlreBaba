package com.ssafy.alrebaba.language.domain;

import com.ssafy.alrebaba.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long LanguageId;

    @Enumerated(EnumType.STRING)
    private LanguageName languageName;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 명시적 생성자 추가
    public Language(LanguageName languageName, Member member) {
        this.languageName = languageName;
        this.member = member;

    }

}
