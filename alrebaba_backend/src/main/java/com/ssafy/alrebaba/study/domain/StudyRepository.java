package com.ssafy.alrebaba.study.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("SELECT s " +
            "FROM Study s " +
            "INNER JOIN StudyParticipant sp ON s.studyId = sp.study.studyId " +
            "WHERE sp.member.memberId = :memberId " +
            "AND sp.participantStatus = 'JOINED' " +
            "ORDER BY s.studyId DESC")
    List<Study> findStudyByMemberId(@Param("memberId") Long memberId);

}
