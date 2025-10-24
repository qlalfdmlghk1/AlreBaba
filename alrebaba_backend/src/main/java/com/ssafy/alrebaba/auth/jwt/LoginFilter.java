package com.ssafy.alrebaba.auth.jwt;

import com.ssafy.alrebaba.auth.application.RedisRefreshTokenService;
import com.ssafy.alrebaba.auth.domain.LoginRequest;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	//    private final Authenticat
	//    ionManager authenticationManager;
	private final JWTUtil jwtUtil;
	// private final RefreshRepository refreshRepository;
	private final RedisRefreshTokenService redisRefreshTokenService;
	private final String accessTokenName;
    private final String refreshTokenName;
//	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
//	 	RefreshRepository refreshRepository) {
//	 	super(authenticationManager);
//	 	this.jwtUtil = jwtUtil;
//	 	this.refreshRepository = refreshRepository;
//	}

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                       RedisRefreshTokenService redisRefreshTokenService,
					   String accessTokenName,
					   String refreshTokenName) {
		super(authenticationManager);
		this.jwtUtil = jwtUtil;
		this.redisRefreshTokenService = redisRefreshTokenService;
		this.accessTokenName = accessTokenName;
		this.refreshTokenName = refreshTokenName;
    }


	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {
		// 요청 방식과 Content-Type 확인
		if (!"POST".equalsIgnoreCase(request.getMethod()) || !request.getContentType().contains("application/json")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		try {
			// JSON 데이터를 읽어 LoginRequest 객체로 변환
			ObjectMapper objectMapper = new ObjectMapper();
			LoginRequest loginData = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

			String username = loginData.getUsername();
			String password = loginData.getPassword();

			if (username == null || password == null) {
				throw new AuthenticationServiceException("Username or Password not provided");
			}

			// UsernamePasswordAuthenticationToken 생성
			UsernamePasswordAuthenticationToken authRequest =
				new UsernamePasswordAuthenticationToken(username, password);

			// Authentication 객체에 상세 정보 설정
			authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

			// AuthenticationManager에 전달
			return this.getAuthenticationManager().authenticate(authRequest);
		} catch (IOException e) {
			throw new AuthenticationServiceException("Failed to parse JSON request", e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) {

		CustomMemberDetails customMemberDetails = (CustomMemberDetails)authentication.getPrincipal();

		String username = customMemberDetails.getUsername();
		Long memberId = customMemberDetails.getMemberId();
		String nickname = customMemberDetails.getNickname();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();

		String role = auth.getAuthority();

		//토큰 생성
		String access = jwtUtil.createAccessJwt( username, role, memberId ,nickname);
		// String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L, memberId);

		//Refresh 토큰 저장
		//        addRefreshEntity(username, refresh, 86400000L);

		// redis 버전
		String refreshToken = redisRefreshTokenService.generateRefreshToken(memberId);
		//응답 설정
		response.setHeader(accessTokenName, access);
		response.addCookie(createCookie(refreshTokenName, refreshToken));
		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) {
		System.out.println("fail");
		response.setStatus(401);
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(14440);
		//cookie.setSecure(true);
		//cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}

	// private void addRefreshEntity(String username, String refresh, Long expiredMs) {
	//
	// 	Date date = new Date(System.currentTimeMillis() + expiredMs);
	//
	// 	RefreshEntity refreshEntity = RefreshEntity.builder()
	// 		.username(username)
	// 		.refresh(refresh)
	// 		.expiration(date.toString())
	// 		.build();
	//
	// 	refreshRepository.save(refreshEntity);
	// }

}
