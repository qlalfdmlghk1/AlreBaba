package com.ssafy.alrebaba.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Setter
@Getter
public abstract class BasicEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime deletedAt;




}