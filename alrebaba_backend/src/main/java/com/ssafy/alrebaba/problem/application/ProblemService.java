package com.ssafy.alrebaba.problem.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ssafy.alrebaba.problem.dto.request.ProblemUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.ssafy.alrebaba.channel.domain.Channel;
import com.ssafy.alrebaba.coding_test.domain.CodingTest;
import com.ssafy.alrebaba.coding_test.domain.CodingTestRepository;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.problem.domain.Problem;
import com.ssafy.alrebaba.problem.domain.ProblemRepository;
import com.ssafy.alrebaba.problem.domain.ProblemSpecification;
import com.ssafy.alrebaba.problem.dto.request.ProblemCreateRequest;
import com.ssafy.alrebaba.problem.dto.response.ProblemResponse;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipantRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ProblemService {

	private final CodingTestRepository codingTestRepository;
	private final StudyParticipantRepository studyParticipantRepository;
	private final ProblemRepository problemRepository;

//	/**
//	 * 문제 생성
//	 *
//	 * @param problemCreateRequest 문제 생성 관련 정보
//	 * @param loginMember 로그인한 회원
//	 */
//	@Transactional
//	public List<Long> create(ProblemCreateRequest problemCreateRequest,
//							 CustomMemberDetails loginMember) throws IllegalAccessException {
//
//		// 1. 코딩테스트가 존재하는 지 확인
//		CodingTest codingTest = codingTestRepository.findById(problemCreateRequest.codingTestId())
//				.orElseThrow(() -> new NotFoundException("코딩테스트를 찾을 수 없습니다."));
//
//		// 2. 권한이 있는 지 확인
//		Channel channel = codingTest.getChannel();
//
//		checkOwner(channel, loginMember);
//
//		return problemCreateRequest.problem().stream()
//			.map(problemDto -> {
//				Problem problem = Problem.builder()
//					.problemUrl(problemDto.problemUrl())
//					.codingTest(codingTest)
//					.build();
//
//				return problemRepository.save(problem).getProblemId(); // 저장 후 ID 반환
//			})
//			.collect(Collectors.toList());
//	}

	/**
	 * 문제 생성
	 *
	 * @param codingTestId 코딩테스트 번호
	 * @param problemId 문제 PK
	 */
	public List<ProblemResponse> get(Long codingTestId,
									Long problemId){
		List<Problem> problemList = problemRepository.findAll(ProblemSpecification.filterBy(codingTestId, problemId));

		return problemList.stream()
			.map(problem -> ProblemResponse.builder()
				.problemId(problem.getProblemId())
					.problemTitle(problem.getProblemTitle())
				.codingTestId(problem.getCodingTest().getCodingTestId())
				.problemUrl(problem.getProblemUrl())
				.build()
			)
			.collect(Collectors.toList());
	}

	/**
	 * 문제 수정
	 *
	 * @param codingTestId 코딩테스트 문제
	 * @param problemId 문제 PK
	 * @param problemUpdateRequest 수정할 문제 url
	 * @param loginMember 로그인한 멤버
	 */
	@Transactional
	public void update(Long codingTestId,
					   Long problemId,
					   ProblemUpdateRequest problemUpdateRequest,
					   CustomMemberDetails loginMember) throws IllegalAccessException {

		// 1. 문제 찾기
		Problem problem = problemRepository.findById(problemId)
						.orElseThrow(() -> new NotFoundException("문제를 찾을 수 없습니다."));

		// 2. 권한 찾기
		Channel channel = codingTestRepository.findById(codingTestId)
						.orElseThrow(() -> new NotFoundException("코딩테스트를 확인 수 없습니다."))
						.getChannel();

		checkOwner(channel, loginMember);

		problem.setProblemTitle(problemUpdateRequest.problemTitle());
		problem.setProblemUrl(problemUpdateRequest.problemUrl());

	}

	public void checkOwner(Channel channel, CustomMemberDetails loginMember) throws IllegalAccessException {
		Optional.of(studyParticipantRepository.findOwnerIdByStudyId(channel.getStudy().getStudyId()))
				.filter(ownerId -> ownerId.equals(loginMember.getMemberId()))
				.orElseThrow(() -> new IllegalAccessException("코딩테스트는 스터디 장만 생성할 수 있습니다."));
	}
	
}
