package com.ssafy.alrebaba.member.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ssafy.alrebaba.common.util.AbstractCodedEnumConverter;
import com.ssafy.alrebaba.common.util.CodedEnum;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum Role implements CodedEnum<String> {
    ADMIN("ROLE_ADMIN", "운영자"),
    USER("ROLE_USER", "로그인 회원"),
    GUEST("ROLE_GUEST", "손님");


    private final String key;
    private final String title;

    Role(String key, String title) {
        this.key = key;
        this.title = title;
    }


    @JsonCreator
    public static Role from(String input) {
        return Arrays.stream(values())
                .filter(role -> role.key.equalsIgnoreCase(input))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @JsonValue
    public String getKey() {
        return this.key;
    }

    public static boolean isSameName(String input, Role role) {
        return role.name()
                .equalsIgnoreCase(input);
    }

    @jakarta.persistence.Converter(autoApply = true)
    static class Converter extends AbstractCodedEnumConverter<Role, String> {
        public Converter() {
            super(Role.class);
        }
    }

}