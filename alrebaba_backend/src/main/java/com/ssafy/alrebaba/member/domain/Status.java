package com.ssafy.alrebaba.member.domain;


import com.ssafy.alrebaba.common.util.AbstractCodedEnumConverter;
import com.ssafy.alrebaba.common.util.CodedEnum;

public enum Status implements CodedEnum<String> {

    ONLINE("ONLINE"),
    ON_ANOTHER_BUSINESS("ON_ANOTHER_BUSINESS"),
    NO_INTERFERENCE("NO_INTERFERENCE"),
    OFF_LINE("OFF_LINE");


    private String key;
    private String detail;

    Status(String key){
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @jakarta.persistence.Converter(autoApply = true)
    static class Converter extends AbstractCodedEnumConverter<Status, String> {
        public Converter() {
            super(Status.class);
        }
    }
}
