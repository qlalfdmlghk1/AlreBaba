package com.ssafy.alrebaba.channel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChannelCreateRequest(
        @Size(min = 2, max = 15, message = "채널 이름은 2자 이상 15자 이하로 입력해주세요.")
        String channelName,

        @NotBlank(message = "채널 타입을 선택해주세요.")
        @Pattern(regexp = "^(CHAT|CODE|MEETING|TEST)$",
                message = "채널은 지정된 채널만 생성 가능합니다.")
        String channelType
) {
}
