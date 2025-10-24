package com.ssafy.alrebaba.code.domain;

import java.util.Arrays;

public enum Language {
	PYTHON("Python"),
	CPP("C++"),
	C("C"),
	JAVA("Java"),
	RUBY("Ruby"),
	C_SHARP("C#"),
	NODE_JS("node.js"),
	GO("Go"),
	RUST("Rust"),
    PLAINTEXT("Plaintext"),
	JAVASCRIPT("Javascript");

	private final String type;

	Language(String type) {
		this.type = type;
	}

	public static Language fromString(String type) {
		return Arrays.stream(values())
			.filter(language -> language.type.equalsIgnoreCase(type)) // 대소문자 무시 비교
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 언어입니다: " + type));

	}

	@Override
	public String toString() {
		return type;
	}
}