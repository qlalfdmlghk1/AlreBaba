package com.ssafy.alrebaba.auth.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

@Getter
@Builder
@RedisHash(value = "refreshToken", timeToLive = 14440)
public class RefreshToken {

	@Id
	private String refreshToken;
	private Long memberId;

}