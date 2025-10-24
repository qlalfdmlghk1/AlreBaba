package com.ssafy.alrebaba.code.dto.request;

import com.ssafy.alrebaba.code.domain.Language;
import com.ssafy.alrebaba.code.domain.Platform;
import jakarta.validation.constraints.NotBlank;


public record CodeCreateRequest(

	@NotBlank(message = "플랫폼은 필수입니다.")
	String platform,

	@NotBlank(message = "코드 제목은 필수입니다.")
	String title,

	@NotBlank(message = "코드는 필수입니다.")
	String context,
	
	String language,

	Long problemId

) {
}
