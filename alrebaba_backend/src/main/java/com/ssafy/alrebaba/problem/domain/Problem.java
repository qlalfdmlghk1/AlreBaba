package com.ssafy.alrebaba.problem.domain;

import com.ssafy.alrebaba.coding_test.domain.CodingTest;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Builder
@Table(name = "problems")
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "problem_id")
	private Long problemId;

	@ManyToOne
	@JoinColumn(name = "coding_test_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private CodingTest codingTest;

	@Setter
	@Column(name = "problem_title", nullable = false)
	private String problemTitle;

	@Setter
	@Column(name = "problem_url", nullable = false)
	private String problemUrl;

}
