package com.ssafy.alrebaba.code.domain;

import java.util.Arrays;

public enum Platform {

	BOJ, Programmers, SWEA, LeetCode, CodeTree, Etc;

	public static Platform fromString(String type){
		return Arrays.stream(values())
			.filter(platform -> platform.name().equals(type))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 플랫폼입니다."));
	}

}
