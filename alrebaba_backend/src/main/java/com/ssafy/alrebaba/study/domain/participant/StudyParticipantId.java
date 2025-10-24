package com.ssafy.alrebaba.study.domain.participant;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StudyParticipantId implements Serializable {

    private Long studyId;

    private Long memberId;
}