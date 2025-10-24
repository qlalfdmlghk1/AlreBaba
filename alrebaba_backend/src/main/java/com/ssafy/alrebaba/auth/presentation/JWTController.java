package com.ssafy.alrebaba.auth.presentation;

import com.ssafy.alrebaba.auth.application.RedisRefreshTokenService;
//import com.ssafy.alrebaba.auth.domain.RefreshEntity;
//import com.ssafy.alrebaba.auth.domain.RefreshRepository;
import com.ssafy.alrebaba.auth.domain.RefreshToken;
import com.ssafy.alrebaba.auth.domain.RefreshTokenRepository;
import com.ssafy.alrebaba.auth.dto.response.AccessAndRefreshToken;
import com.ssafy.alrebaba.auth.exception.AuthException;
import com.ssafy.alrebaba.auth.jwt.JWTUtil;

import com.ssafy.alrebaba.member.exception.MemberException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JWTController {
	private final JWTUtil jwtUtil;
//	private RefreshRepository refreshRepository;
	private final RedisRefreshTokenService redisRefreshTokenService;
	@Value("${spring.jwt.access-token-name}") String accessTokenName;
	@Value("${spring.jwt.refresh-token-name}") String refreshTokenName;
	@Value("${spring.jwt.oauth-token-name}") String oauthTokenName;
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws MemberException.MemberBadRequestException, AuthException.AuthBadRequestException {

		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(refreshTokenName)) {
				refresh = cookie.getValue();
			}
		}

		if (refresh == null) {

			//response status code
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		//expired check
//		try {
//			jwtUtil.isExpired(refresh);
//		} catch (ExpiredJwtException e) {
//
//			//response status code
//			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
//		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		// String category = jwtUtil.getCategory(refresh);
		//
		// if (!category.equals("refresh")) {
		//
		// 	//response status code
		// 	return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		// }
		//
		// //DB에 저장되어 있는지 확인
		// Boolean isExist = refreshRepository.existsByRefresh(refresh);
		// if (!isExist) {
		//
		// 	//response body
		// 	return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		// }

//		String username = jwtUtil.getUsername(refresh);
//		String role = jwtUtil.getRole(refresh).getKey();
//		Long memberId = jwtUtil.getMemberId(refresh);

		//make new JWT
//		String newAccess = jwtUtil.createJwt("access", username, role, memberId);
		// String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L, memberId);
		//Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
		// refreshRepository.deleteByRefresh(refresh);
		// addRefreshEntity(username, newRefresh, 86400000L);

		//redis
		AccessAndRefreshToken accessAndRefreshToken = redisRefreshTokenService.getAccessAndRefreshToken(refresh);

		//response
		response.setHeader(accessTokenName, accessAndRefreshToken.accessToken());
		response.addCookie(createCookie(refreshTokenName, accessAndRefreshToken.refreshToken()));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/oauth2")
	public ResponseEntity<?> getJWTByCookie(HttpServletRequest request, HttpServletResponse response) {
		String authorization = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(oauthTokenName)) {
				authorization = cookie.getValue();
			}
		}

		if (authorization == null) {
			//response status code
			return new ResponseEntity<>("authorization token null", HttpStatus.BAD_REQUEST);
		}

		//expired check
		try {
			jwtUtil.isExpired(authorization);
		} catch (ExpiredJwtException e) {

			//response status code
			return new ResponseEntity<>("authorization token expired", HttpStatus.BAD_REQUEST);
		}
		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(authorization);

		if (!category.equals(oauthTokenName)) {
			//response status code
			return new ResponseEntity<>("invalid authorization token", HttpStatus.BAD_REQUEST);
		}


		String username = jwtUtil.getUsername(authorization);
		String role = jwtUtil.getRole(authorization).getKey();
		Long memberId = jwtUtil.getMemberId(authorization);
		String nickname = jwtUtil.getNickname(authorization);

		//make new JWT
		String newAccess = jwtUtil.createAccessJwt( username, role, memberId, nickname);

		//response
		response.setHeader(accessTokenName, newAccess);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(14440);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}

//	private void addRefreshEntity(String username, String refresh, Long expiredMs) {
//
//		Date date = new Date(System.currentTimeMillis() + expiredMs);
//
//		RefreshEntity refreshEntity = RefreshEntity.builder()
//			.username(username)
//			.refresh(refresh)
//			.expiration(date.toString())
//			.build();
//
//		refreshRepository.save(refreshEntity);
//	}

}