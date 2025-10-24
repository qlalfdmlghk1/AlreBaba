package com.ssafy.alrebaba.member.application;

import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.member.exception.MemberErrorCode;
import com.ssafy.alrebaba.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService  implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member userData = Optional.ofNullable(memberRepository.findByUsername(username))
                .orElseThrow(() -> new MemberException.MemberConflictException(MemberErrorCode.MEMBER_NOT_FOUND, username));

        if (userData != null) {
            return new CustomMemberDetails(userData);
        }


        throw new UsernameNotFoundException("User not found with email: " + username);
    }

}
