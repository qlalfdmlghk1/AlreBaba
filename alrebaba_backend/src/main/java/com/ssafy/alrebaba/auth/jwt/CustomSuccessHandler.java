package com.ssafy.alrebaba.auth.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import com.ssafy.alrebaba.auth.application.RedisRefreshTokenService;
import com.ssafy.alrebaba.auth.domain.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ssafy.alrebaba.auth.domain.CustomOAuth2User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${spring.jwt.oauth-token-name}") String oauthTokenName;
    @Value("${spring.jwt.refresh-token-name}") String refreshTokenName;

    private final JWTUtil jwtUtil;
    private final RedisRefreshTokenService redisRefreshTokenService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();
        Long memberId = customUserDetails.getMemberId();
//        String nickname = customUserDetails.getName();

//        String token = jwtUtil.createOauthJwt(username, role, memberId, nickname);
        String refreshToken = redisRefreshTokenService.generateRefreshToken(memberId);

        response.addCookie(createCookie(refreshTokenName, refreshToken));
        response.sendRedirect("https://i12a702.p.ssafy.io/");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}