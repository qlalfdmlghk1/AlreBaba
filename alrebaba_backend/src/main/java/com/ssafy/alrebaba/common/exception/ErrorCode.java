package com.ssafy.alrebaba.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorCode<T> {

    private final String message;
    private String code;
    private T info;

    public ErrorCode(String code, String message, T info) {
        this.code = code;
        this.message = message;
        this.info = info;
    }

    public ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorCode(String message) {
        this.message = message;
    }

}