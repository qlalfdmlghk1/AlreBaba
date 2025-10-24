package com.ssafy.alrebaba.auth.domain;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}