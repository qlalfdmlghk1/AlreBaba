package com.ssafy.alrebaba.friend.domain;

import com.ssafy.alrebaba.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendId>, FriendRepositoryCustom {

    // 기존 메서드들 (친구 요청, 받은 요청 등)
    List<Friend> findByRequestMemberOrAcceptMember(Member requestMember, Member acceptMember);

    List<Friend> findByRequestMemberAndStatus(Member requestMember, FriendStatus status);

    List<Friend> findByAcceptMemberAndStatus(Member acceptMember, FriendStatus status);

    Optional<Friend> findByRequestMemberAndAcceptMember(Member requestMember, Member acceptMember);

    boolean existsByRequestMemberAndAcceptMember(Member requestMember, Member acceptMember);

    void deleteByRequestMemberAndAcceptMember(Member requestMember, Member acceptMember);

    List<Friend> findByRequestMemberAndStatusOrAcceptMemberAndStatus(
            Member requestMember, FriendStatus requestStatus,
            Member acceptMember, FriendStatus acceptStatus
    );

    @Query("SELECT COUNT(f) > 0 FROM Friend f " +
            "WHERE (f.acceptMember.memberId = :#{#member1.memberId} AND f.requestMember.memberId = :#{#member2.memberId} " +
            "OR f.acceptMember.memberId = :#{#member2.memberId} AND f.requestMember.memberId = :#{#member1.memberId}) " +
            "AND f.status = 'FOLLOWING'")
    boolean existsFriendship(@Param("member1") Member member1,
                             @Param("member2") Member member2);

    // 받은 친구 요청 조회용 메서드 (기존 분리된 방식; 필요 시 유지)
    List<Friend> findByAcceptMemberAndStatusOrderByRequestMemberMemberIdDesc(
            Member acceptMember,
            FriendStatus status,
            Pageable pageable
    );

    List<Friend> findByAcceptMemberAndStatusAndRequestMemberMemberIdLessThanOrderByRequestMemberMemberIdDesc(
            Member acceptMember,
            FriendStatus status,
            Long lastId,
            Pageable pageable
    );

    // 보낸 친구 요청 목록 조회 (NoOffset 페이징)
    List<Friend> findByRequestMemberAndStatusOrderByCreatedAtDesc(
            Member requestMember,
            FriendStatus status,
            Pageable pageable
    );

    List<Friend> findByRequestMemberAndStatusAndCreatedAtLessThanOrderByCreatedAtDesc(
            Member requestMember,
            FriendStatus status,
            LocalDateTime lastCreatedAt,
            Pageable pageable
    );
}
