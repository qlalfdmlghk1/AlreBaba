package com.ssafy.alrebaba.code.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ssafy.alrebaba.channel.domain.Channel;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.problem.domain.Problem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "codes")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Code {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "code_id")
	private Long codeId;

	@ManyToOne
	@JoinColumn(name = "channel_id", nullable = true)
	private Channel channel;

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "platform", nullable = false)
	private Platform platform;

	@Setter
	@Column(name = "title")
	private String title;

	@Setter
	@Column(name = "context", columnDefinition = "TEXT")
	private String context;

	@Setter
	@Column(name = "language", nullable = false)
	private Language language;

	@Column(name = "create_at")
	@CreatedDate
	private LocalDateTime createAt;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "problem_id", nullable = true)
	private Problem problem;

}
