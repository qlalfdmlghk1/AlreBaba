package com.ssafy.alrebaba.code.domain;



import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class CodeSpecification {

	public static Specification<Code> filterBy(Long codeId,
											   Long channelId,
											   String platform,
											   String title,
											   String language,
											   Long memberId,
											   Long problemId,
											   Long lastId) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (codeId != null) {
				predicates.add(criteriaBuilder.equal(root.get("codeId"), codeId));
			}
			if (channelId != null) {
				predicates.add(criteriaBuilder.equal(root.get("channel").get("channelId"), channelId));
			}
			if (platform != null) {
				predicates.add(criteriaBuilder.equal(root.get("platform"), Platform.fromString(platform)));
			}
			if (title != null && !title.isEmpty()) {
				predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
			}
			if (language != null) {
				predicates.add(criteriaBuilder.equal(root.get("language"), Language.fromString(language)));
			}
			if (memberId != null) {
				predicates.add(criteriaBuilder.equal(root.get("member").get("memberId"), memberId));
			}
			if (problemId != null) {
				predicates.add(criteriaBuilder.equal(root.get("problem").get("problemId"), problemId));
			}
			if (lastId != null) {
				predicates.add(criteriaBuilder.lt(root.get("codeId"), lastId));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
