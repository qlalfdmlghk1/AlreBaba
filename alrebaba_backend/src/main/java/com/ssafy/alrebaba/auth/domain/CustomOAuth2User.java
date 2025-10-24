package com.ssafy.alrebaba.auth.domain;

import com.ssafy.alrebaba.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final Member member;

    public CustomOAuth2User(Member member) {
        this.member = member;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return member.getRole().getKey();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return member.getNickname();
    }

    public Long getMemberId(){

        return member.getMemberId();
    }

    public String getUsername() {

        return member.getUsername();
    }
}