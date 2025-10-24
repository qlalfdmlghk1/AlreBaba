package com.ssafy.alrebaba.study.domain.participant;

import com.ssafy.alrebaba.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, StudyParticipantId> {

    @Query("SELECT sp.member.memberId FROM StudyParticipant sp WHERE sp.study.studyId = :studyId AND sp.participantStatus = 'JOINED'")
    List<Long> findJoinedMemberIdsByStudyId(@Param("studyId") Long studyId);

    @Query("SELECT sp.member.memberId FROM StudyParticipant sp WHERE sp.study.studyId = :studyId AND sp.participantRole = 'OWNER'")
    Long findOwnerIdByStudyId(@Param("studyId") Long studyId);

    @Query("SELECT COUNT(sp) > 0 FROM StudyParticipant sp WHERE sp.study.studyId = :studyId AND sp.member.memberId = :memberId AND sp.participantStatus = 'JOINED'")
    boolean existsByStudyIdAndMemberIdJoin(@Param("studyId") Long studyId, @Param("memberId") Long memberId);

    @Query("SELECT sp.member FROM StudyParticipant sp " +
            "WHERE sp.study.studyId = :studyId " +
            "AND sp.member.memberId = :memberId " +
            "AND sp.participantStatus = 'JOINED'")
    Optional<Member> findJoinedMemberByStudyIdAndMemberId(@Param("studyId") Long studyId, @Param("memberId") Long memberId);

}

