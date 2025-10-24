package com.ssafy.alrebaba.channel.dto.request;

import jakarta.validation.constraints.Size;

public record ChannelNameUpdateRequest(

        @Size(min = 2, max = 15, message = "채널 이름은 2자 이상 15자 이하로 입력해주세요.")
        String channelName

) {
}
