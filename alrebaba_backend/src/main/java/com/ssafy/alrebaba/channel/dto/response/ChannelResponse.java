package com.ssafy.alrebaba.channel.dto.response;

import com.ssafy.alrebaba.channel.domain.Channel;
import lombok.Builder;

@Builder
public record ChannelResponse(
    Long channelId,
    String channelName,
    String channelType
) {

    public static ChannelResponse from(Channel channel) {
        return ChannelResponse.builder()
                .channelId(channel.getChannelId())
                .channelName(channel.getChannelName())
                .channelType(channel.getChannelType().name())
                .build();
    }
}
