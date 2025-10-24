package com.ssafy.alrebaba.code.domain;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface CodeRepository extends JpaRepository<Code, Long>, JpaSpecificationExecutor<Code> {
    @Query("SELECT COUNT(c) > 0 FROM Code c WHERE c.problem.problemId = :problemId AND c.member.memberId = :memberId")
    boolean existsByProblemIdAndMemberId(@Param("problemId") Long problemId, @Param("memberId") Long memberId);

    Page<Code> findAll(Specification<Code> spec, Pageable pageable);

}
