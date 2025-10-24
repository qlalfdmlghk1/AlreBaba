package com.ssafy.alrebaba.auth.dto.response;

import java.util.Map;

public class GitHubResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    public GitHubResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        return attribute.get("login").toString(); // GitHub 닉네임
    }

    @Override
    public String getProfile() {
        return attribute.get("avatar_url").toString(); // GitHub 프로필 이미지
    }
}
