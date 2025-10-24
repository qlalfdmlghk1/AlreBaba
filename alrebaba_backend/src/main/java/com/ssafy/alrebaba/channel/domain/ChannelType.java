package com.ssafy.alrebaba.channel.domain;

import java.util.Arrays;

public enum ChannelType {
    CHAT, CODE, MEETING, TEST;

    // 문자열을 ChannelType과 매핑
    public static ChannelType fromString(String type) {
        return Arrays.stream(values())
                .filter(channelType-> channelType.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 채널입니다."));
    }
}