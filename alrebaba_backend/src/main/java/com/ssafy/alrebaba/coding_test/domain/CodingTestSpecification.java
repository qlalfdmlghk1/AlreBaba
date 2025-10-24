package com.ssafy.alrebaba.coding_test.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class CodingTestSpecification {

	public static Specification<CodingTest> filterBy(Long codingTestId,
		Long channelId,
		LocalDateTime startTime,
		LocalDateTime endTime) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// codingTestId 필터
			if (codingTestId != null) {
				predicates.add(criteriaBuilder.equal(root.get("codingTestId"), codingTestId));
			}

			// channelId 필터
			if (channelId != null) {
				predicates.add(criteriaBuilder.equal(root.get("channel").get("channelId"), channelId));
			}

			// startTime 필터 (startTime보다 이후의 데이터)
			if (startTime != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startTime));
			}

			// endTime 필터 (endTime보다 이전의 데이터)
			if (endTime != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), endTime));
			}

			// 모든 조건을 AND로 묶어서 반환
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
