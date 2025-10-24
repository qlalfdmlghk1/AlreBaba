package com.ssafy.alrebaba.problem.domain;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class ProblemSpecification {
	public static Specification<Problem> filterBy(Long codingTestId, Long problemId) {
		return (root, query, criteriaBuilder) -> {
			Predicate predicate = criteriaBuilder.conjunction();

			// codingTestId가 주어지면 조건 추가
			if (codingTestId != null) {
				predicate = criteriaBuilder.and(predicate,
					criteriaBuilder.equal(root.get("codingTest").get("codingTestId"), codingTestId));
			}

			// problemId가 주어지면 조건 추가
			if (problemId != null) {
				predicate = criteriaBuilder.and(predicate,
					criteriaBuilder.equal(root.get("problemId"), problemId));
			}

			return predicate;
		};
	}
}
