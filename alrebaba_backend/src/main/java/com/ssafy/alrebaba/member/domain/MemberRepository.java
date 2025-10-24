package com.ssafy.alrebaba.member.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);  // 기존 코드 유지
    Boolean existsByUsername(String username);
    Boolean existsByNickname(String nickname);
    void delete(Member member);

    /**
     * uniqueId가 검색어를 포함하는 회원을 검색합니다.
     * 단, uniqueId가 검색어와 정확히 일치하면 우선적으로 정렬합니다.
     * (정확히 일치하는 경우 CASE 값 0, 나머지는 1로 처리)
     */
    @Query("SELECT m FROM Member m " +
            "WHERE m.uniqueId LIKE CONCAT('%', :searchKeyword, '%') " +
            "ORDER BY (CASE WHEN m.uniqueId = :searchKeyword THEN 0 ELSE 1 END), m.memberId DESC")
    List<Member> searchByUniqueIdWithExactFirst(@Param("searchKeyword") String searchKeyword, Pageable pageable);

    /**
     * 페이징 처리를 위한 두 번째 이후 페이지 조회.
     * 마지막 memberId보다 작은 회원들 중에서 검색 조건을 만족하는 회원을 조회합니다.
     */
    @Query("SELECT m FROM Member m " +
            "WHERE m.uniqueId LIKE CONCAT('%', :searchKeyword, '%') " +
            "AND m.memberId < :lastId " +
            "ORDER BY (CASE WHEN m.uniqueId = :searchKeyword THEN 0 ELSE 1 END), m.memberId DESC")
    List<Member> searchByUniqueIdWithExactFirstAndMemberIdLessThan(@Param("searchKeyword") String searchKeyword,
                                                                   @Param("lastId") Long lastId,
                                                                   Pageable pageable);
}
