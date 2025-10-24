package com.ssafy.alrebaba.code.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record CodePageResponse(
	List<CodeResponse> content,
	Boolean hasNext,
	Long lastId
) {
}
