package com.ssafy.alrebaba.auth.application;

import com.ssafy.alrebaba.auth.domain.CustomOAuth2User;
import com.ssafy.alrebaba.auth.dto.response.GitHubResponse;
import com.ssafy.alrebaba.auth.dto.response.KakaoResponse;
import com.ssafy.alrebaba.auth.dto.response.OAuth2Response;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import com.ssafy.alrebaba.member.domain.Role;
import com.ssafy.alrebaba.member.domain.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        // Kakao 로그인 처리
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        // GitHub 로그인 등 추가할 경우
         else if (registrationId.equals("github")) {
            oAuth2Response = new GitHubResponse(oAuth2User.getAttributes());
         }
        else {
            return null;
        }

        // 리소스 서버에서 발급받은 정보로 사용자를 특정할 아이디 생성
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Member existData = memberRepository.findByUsername(username);

        if (existData == null) {
            // 새로운 사용자 등록

            Member member = Member.builder()
                    .username(username) // OAuth에서 제공된 고유 ID
                    .nickname(oAuth2Response.getName()) // 사용자 이름
                    .profileImage(oAuth2Response.getProfile()) // 프로필 이미지 URL
                    .role(Role.from("ROLE_USER")) // 기본 역할
                    .status(Status.ONLINE) // 기본 상태(온라인)
                    .build();

            memberRepository.save(member);

            String uniqueId = member.getNickname() + "@" + member.getMemberId();
            member.setUniqueId(uniqueId);

            memberRepository.save(member);

            return new CustomOAuth2User(member);

        } else {
            // 기존 사용자 정보 업데이트
            existData.setNickname(oAuth2Response.getName()); // 닉네임 업데이트
            existData.setProfileImage(oAuth2Response.getProfile()); // 프로필 이미지 URL 업데이트
            memberRepository.save(existData);

            return new CustomOAuth2User(existData);
        }

    }

}
