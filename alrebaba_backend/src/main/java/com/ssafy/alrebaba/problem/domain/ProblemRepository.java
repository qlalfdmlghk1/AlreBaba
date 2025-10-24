package com.ssafy.alrebaba.problem.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProblemRepository extends JpaRepository<Problem, Long> , JpaSpecificationExecutor<Problem> {
}
