package com.ssafy.alrebaba.member.application;

import com.ssafy.alrebaba.common.storage.application.ImageUtil;
import com.ssafy.alrebaba.interest.domain.Interest;
import com.ssafy.alrebaba.interest.domain.InterestName;
import com.ssafy.alrebaba.language.domain.Language;
import com.ssafy.alrebaba.language.domain.LanguageName;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import com.ssafy.alrebaba.member.domain.Role;
import com.ssafy.alrebaba.member.domain.Status;
import com.ssafy.alrebaba.member.dto.request.*;
import com.ssafy.alrebaba.member.dto.response.*;
import com.ssafy.alrebaba.member.exception.MemberErrorCode;
import com.ssafy.alrebaba.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageUtil imageUtil;

    @Value("${basic.image.profile-url}")
    private String basicProfileImageUrl; // 기본 프로필 이미지 URL

    // 헬퍼 메서드: 회원을 찾거나 없으면 예외 발생
    private Member findMemberOrThrow(Long memberId) throws MemberException.MemberBadRequestException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (!optionalMember.isPresent()) {
            throw new MemberException.MemberBadRequestException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        return optionalMember.get();
    }

    // 1. 회원가입
    @Transactional
    public MemberJoinResponse createMemberService(MemberJoinRequest memberJoinRequest) {
        if (memberRepository.existsByUsername(memberJoinRequest.username())) {
            throw new MemberException.MemberConflictException(MemberErrorCode.MEMBER_ALREADY_EXIST, memberJoinRequest.username());
        }

        Member member = Member.builder()
                .username(memberJoinRequest.username())
                .password(bCryptPasswordEncoder.encode(memberJoinRequest.password()))
                .nickname(memberJoinRequest.nickname())
                .role(Role.USER)
                .status(Status.ONLINE)
                .profileImage(basicProfileImageUrl)
                .build();

        // Unique ID 설정 (저장 전에는 memberId가 null일 수 있으므로, 별도 로직이나 @PostPersist를 고려)
        String uniqueId = member.getNickname() + "@" + member.getMemberId();
        member.setUniqueId(uniqueId);
        memberRepository.save(member);

        return MemberJoinResponse.builder()
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .uniqueId(member.getUniqueId())
                .status(member.getStatus())
                .profileImage(member.getProfileImage())
                .build();
    }

    // 2. 회원 정보 조회 (로그인한 회원)
    @Transactional(readOnly = true)
    public MemberGetResponse getMemberService(CustomMemberDetails loginMember) throws MemberException.MemberBadRequestException {
        Long memberId = loginMember.getMemberId();
        Member member = findMemberOrThrow(memberId);
        return MemberGetResponse.builder()
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .profileImage(imageUtil.getPreSignedUrl(member.getProfileImage()))
                .status(member.getStatus())
                .uniqueId(member.getUniqueId())
                .isAlarmOn(member.getIsAlarmOn())
                .createdAt(member.getCreatedAt())
                .build();
    }

    // 2. 회원 정보 조회 (memberId로 조회)
    @Transactional(readOnly = true)
    public MemberGetResponse getMemberService(Long memberId) throws MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(memberId);
        return MemberGetResponse.builder()
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .profileImage(imageUtil.getPreSignedUrl(member.getProfileImage()))
                .status(member.getStatus())
                .uniqueId(member.getUniqueId())
                .isAlarmOn(member.getIsAlarmOn())
                .interests(member.getInterestList().stream().map(Interest::getInterestName).toList())
                .languages(member.getLanguageList().stream().map(Language::getLanguageName).toList())
                .createdAt(member.getCreatedAt())
                .build();
    }

    // 이메일 중복확인
    public MemberUsernameDuplicateResponse duplicateUsernameService(String username) {
        boolean isDuplicated = memberRepository.existsByUsername(username);
        return MemberUsernameDuplicateResponse.builder()
                .isDuplicated(isDuplicated)
                .build();
    }

    // 닉네임 중복확인
    public boolean duplicateNicknameService(String nickName) {
        return memberRepository.existsByNickname(nickName);
    }

    // (기존) 전체 정보 수정 API (닉네임, 관심사, 선호 언어 함께)
    @Transactional
    public MemberUpdateResponse updateMember(MemberUpdateRequest memberUpdateRequest, CustomMemberDetails loginMember)
            throws MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(loginMember.getMemberId());

        // 닉네임 수정 (중복 체크)
        if (memberUpdateRequest.nickname() != null && !memberUpdateRequest.nickname().equals(member.getNickname())) {
            if (memberRepository.existsByNickname(memberUpdateRequest.nickname())) {
                throw new MemberException.MemberConflictException(MemberErrorCode.ILLEGAL_NICKNAME_ALREADY_EXISTS, memberUpdateRequest.nickname());
            }
            member.setNickname(memberUpdateRequest.nickname());
            member.setUniqueId(member.getNickname() + "@" + member.getMemberId());
        }

        // 관심사 수정
        if (memberUpdateRequest.interests() != null) {
            updateMemberInterests(member, memberUpdateRequest.interests());
        }

        // 선호 언어 수정
        if (memberUpdateRequest.languages() != null) {
            updateMemberLanguages(member, memberUpdateRequest.languages());
        }

        memberRepository.save(member);
        return buildMemberUpdateResponse(member);
    }

    // 별도 닉네임 수정 API
    @Transactional
    public MemberUpdateResponse updateNickname(MemberNicknameUpdateRequest request, CustomMemberDetails loginMember)
            throws MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(loginMember.getMemberId());
        if (!request.nickname().equals(member.getNickname())) {
            if (memberRepository.existsByNickname(request.nickname())) {
                throw new MemberException.MemberConflictException(
                        MemberErrorCode.ILLEGAL_NICKNAME_ALREADY_EXISTS, request.nickname());
            }
            member.setNickname(request.nickname());
            member.setUniqueId(member.getNickname() + "@" + member.getMemberId());
            memberRepository.save(member);
        }
        return buildMemberUpdateResponse(member);
    }

    // 별도 관심사 수정 API
    @Transactional
    public MemberUpdateResponse updateInterests(MemberInterestsUpdateRequest request, CustomMemberDetails loginMember)
            throws MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(loginMember.getMemberId());
        updateMemberInterests(member, request.interests());
        memberRepository.save(member);
        return buildMemberUpdateResponse(member);
    }

    // 별도 선호 언어 수정 API
    @Transactional
    public MemberUpdateResponse updateLanguages(MemberLanguagesUpdateRequest request, CustomMemberDetails loginMember)
            throws MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(loginMember.getMemberId());
        updateMemberLanguages(member, request.languages());
        memberRepository.save(member);
        return buildMemberUpdateResponse(member);
    }

    /**
     * 관심사 업데이트: 요청된 관심사 값은 Enum의 key 값(예:"인공지능")이어야 함
     */
    private void updateMemberInterests(Member member, List<String> newInterestInputs) {
        // 기존 관심사 중 새 요청에 없는 항목 제거
        member.getInterestList().removeIf(interest ->
                newInterestInputs.stream().noneMatch(input -> {
                    try {
                        InterestName enumVal = getInterestEnum(input);
                        return enumVal == interest.getInterestName();
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
        );

        // 새 관심사 중 추가할 항목 처리
        for (String input : newInterestInputs) {
            InterestName enumVal = getInterestEnum(input);
            boolean alreadyExists = member.getInterestList().stream()
                    .anyMatch(interest -> interest.getInterestName() == enumVal);
            if (!alreadyExists) {
                Interest interest = new Interest(enumVal, member);
                member.getInterestList().add(interest);
            }
        }
    }

    /**
     * 선호 언어 업데이트
     */
    private void updateMemberLanguages(Member member, List<String> newLanguageNames) {
        // 기존 언어 목록에서, newLanguageNames에 해당하는 name 또는 key가 없으면 제거
        member.getLanguageList().removeIf(language ->
                newLanguageNames.stream().noneMatch(input ->
                        input.equalsIgnoreCase(language.getLanguageName().name()) ||
                                input.equalsIgnoreCase(language.getLanguageName().getKey())
                )
        );

        // 새로운 언어 추가
        for (String languageName : newLanguageNames) {
            boolean alreadyExists = member.getLanguageList().stream()
                    .anyMatch(language ->
                            language.getLanguageName().name().equalsIgnoreCase(languageName) ||
                                    language.getLanguageName().getKey().equalsIgnoreCase(languageName)
                    );
            if (!alreadyExists) {
                try {
                    LanguageName langEnum = getLanguageEnum(languageName);
                    Language language = new Language(langEnum, member);
                    member.getLanguageList().add(language);
                } catch (IllegalArgumentException e) {
                    log.error("🔴 ENUM 변환 실패: {}", languageName, e);
                }
            }
        }
    }


    /**
     * 관심사 ENUM 변환: 입력값 -> InterestName 변환
     */
    private InterestName getInterestEnum(String interestInput) {
        for (InterestName interest : InterestName.values()) {
            if (interest.name().equalsIgnoreCase(interestInput) || interest.getKey().equalsIgnoreCase(interestInput)) {
                return interest;
            }
        }
        throw new IllegalArgumentException("지원되지 않는 관심사: " + interestInput);
    }

    /**
     * 언어 ENUM 변환: 입력값 -> LanguageName 변환
     */
    private LanguageName getLanguageEnum(String languageInput) {
        for (LanguageName lang : LanguageName.values()) {
            if (lang.name().equalsIgnoreCase(languageInput) || lang.getKey().equalsIgnoreCase(languageInput)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("지원되지 않는 언어: " + languageInput);
    }

    /**
     * 회원 업데이트 응답 생성: interests는 Enum의 key, languages는 Enum의 name 반환
     */
    private MemberUpdateResponse buildMemberUpdateResponse(Member member) {
        return MemberUpdateResponse.builder()
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .status(member.getStatus().name())
                .uniqueId(member.getUniqueId())
                .updatedAt(member.getUpdatedAt())
                .alarmOn(member.getIsAlarmOn())
                .interests(member.getInterestList().stream().map(i -> i.getInterestName().getKey()).toList())
                .languages(member.getLanguageList().stream().map(l -> l.getLanguageName().name()).toList())
                .build();
    }

    // 4. 프로필 이미지 업데이트
    @Transactional
    public String updateProfileImage(Long memberId, MultipartFile multipartFile, CustomMemberDetails loginMember)
            throws IOException, MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(loginMember.getMemberId());

        // (선택 사항) 로그인한 사용자와 업데이트 대상 확인
        if (!Objects.equals(member.getMemberId(), loginMember.getMemberId())) {
            throw new MemberException.MemberBadRequestException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        // 이미지 업로드
        String newProfileImageUrl = imageUtil.store(multipartFile, "profile");
        member.setProfileImage(newProfileImageUrl);
        memberRepository.save(member);
        return newProfileImageUrl;
    }

    // 비밀번호 수정
    @Transactional
    public void updatePassword(MemberPasswordUpdateRequest passwordUpdateRequest, CustomMemberDetails loginMember)
            throws MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(loginMember.getMemberId());
        if (!bCryptPasswordEncoder.matches(passwordUpdateRequest.oldPassword(), member.getPassword())) {
            throw new MemberException.MemberBadRequestException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        member.setPassword(bCryptPasswordEncoder.encode(passwordUpdateRequest.newPassword()));
        memberRepository.save(member);
    }

    // 상태 변경
    @Transactional
    public MemberStatusUpdateResponse updateStatus(MemberStatusUpdateRequest statusUpdateRequest, CustomMemberDetails loginMember)
            throws MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(loginMember.getMemberId());
        member.setStatus(statusUpdateRequest.status());
        memberRepository.save(member);
        return MemberStatusUpdateResponse.builder()
                .memberId(member.getMemberId())
                .status(member.getStatus())
                .build();
    }

    // 회원 알림 설정
    @Transactional
    public MemberAlarmUpdateResponse updateAlarmStatus(MemberAlarmUpdateRequest request, CustomMemberDetails loginMember)
            throws MemberException.MemberBadRequestException {
        Member member = findMemberOrThrow(loginMember.getMemberId());
        member.setIsAlarmOn(request.isAlarmOn());
        memberRepository.save(member);
        return MemberAlarmUpdateResponse.builder()
                .memberId(member.getMemberId())
                .isAlarmOn(member.getIsAlarmOn())
                .build();
    }

    // 회원 삭제 (idempotent 처리)
    @Transactional
    public void deleteMember(CustomMemberDetails loginMember) throws MemberException.MemberBadRequestException {
        Optional<Member> optionalMember = memberRepository.findById(loginMember.getMemberId());
        optionalMember.ifPresent(memberRepository::delete);
    }
}
