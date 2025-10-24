package com.ssafy.alrebaba.code.dto.request;

import jakarta.validation.constraints.NotNull;

public record CodePatchRequest(
        @NotNull
        Long codeId,

        String platform,

        String title,

        String context,

        String language

){
}
