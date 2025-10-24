package com.ssafy.alrebaba.code.dto.request;

import com.ssafy.alrebaba.code.domain.Language;
import com.ssafy.alrebaba.code.domain.Platform;

public record CodeFilterRequest(
	Long codeId,
	Long channelId,
	String platform,
	String title,
	String language,
	Long memberId,
	Long problemId,
	Long lastId,
	Integer count
) {
}
