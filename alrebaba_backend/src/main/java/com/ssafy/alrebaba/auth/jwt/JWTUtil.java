package com.ssafy.alrebaba.auth.jwt;


import com.ssafy.alrebaba.member.domain.Role;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final Long expiredTime;
    private final String accessTokenName;
    private final String oauthTokenName;
    public JWTUtil(@Value("${spring.jwt.secret}")String secret,
                   @Value("${spring.jwt.access-token-expire}") Long expiredTime,
                   @Value("${spring.jwt.access-token-name}") String accessTokenName,
                   @Value("${spring.jwt.oauth-token-name}") String oauthTokenName) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.expiredTime = expiredTime;
        this.accessTokenName = accessTokenName;
        this.oauthTokenName = oauthTokenName;
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }


    public Role getRole(String token) {
        String roleKey = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
        return Role.from(roleKey);
    }

    public Long getMemberId(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String getNickname(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);
    }


    public String createJwt(String category, String username, String role, Long memberId, String nickname) {

        return Jwts.builder()
                .claim("id", memberId)
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .claim("nickname",nickname)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(secretKey)
                .compact();
    }

    public String createAccessJwt(String username, String role, Long memberId, String nickname){
        return createJwt(accessTokenName, username, role,memberId, nickname);
    };

    public String createOauthJwt(String username, String role, Long memberId, String nickname){
        return createJwt(oauthTokenName, username, role, memberId, nickname);
    }

}