package com.ssafy.alrebaba.coding_test.application;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.ssafy.alrebaba.channel.domain.Channel;
import com.ssafy.alrebaba.channel.domain.ChannelRepository;
import com.ssafy.alrebaba.channel.domain.ChannelType;
import com.ssafy.alrebaba.coding_test.domain.CodingTest;
import com.ssafy.alrebaba.coding_test.domain.CodingTestRepository;
import com.ssafy.alrebaba.coding_test.domain.CodingTestSpecification;
import com.ssafy.alrebaba.coding_test.dto.request.CodingTestCreateRequest;
import com.ssafy.alrebaba.coding_test.dto.request.CodingTestFilterRequest;
import com.ssafy.alrebaba.coding_test.dto.response.CodingTestResponse;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.problem.domain.Problem;
import com.ssafy.alrebaba.problem.domain.ProblemRepository;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodingTestService {

    private final CodingTestRepository codingTestRepository;
    private final ChannelRepository channelRepository;
    private final StudyParticipantRepository studyParticipantRepository;
    private final ProblemRepository problemRepository;

    /**
     * 코딩테스트 생성
     *
     * @param codingTestCreateRequest 생성할 코딩테스트 정보
     * @param loginMember 생성하는 유저의 정보
     */
    @Transactional
    public Long create(CodingTestCreateRequest codingTestCreateRequest,
                       CustomMemberDetails loginMember) throws BadRequestException, IllegalAccessException {


        // 1. 코딩테스트 종료 시간이 시작 시간보다 나중인지 확인
        Optional.of(codingTestCreateRequest.endTime())
                .filter(endTime -> endTime.isAfter(codingTestCreateRequest.startTime()))
                .orElseThrow(() -> new BadRequestException("종료 시간이 시작 시간보다 클 수 없습니다."));

        // 2. 채널이 존재하는 지 확인
        Channel channel = channelRepository.findById(codingTestCreateRequest.channelId())
                .orElseThrow(() -> new NotFoundException("채널을 찾을 수 없습니다."));

        // 3. 채널이 코딩테스트 채널인지 확인
        if(channel.getChannelType() != ChannelType.TEST){
            throw new BadRequestException("코딩테스트 채널에서만 생성할 수 있습니다.");
        }

        // 4. 채널이 이미 코딩테스트를 가지고 있는지 확인
        if(codingTestRepository.existsByChannel(channel)){
            throw new BadRequestException("이미 코딩테스트가 생성된 채널 입니다.");
        }

        // 5. 권한이 있는 지 확인
        checkOwner(channel, loginMember);

        CodingTest codingTest = CodingTest.builder()
                .channel(channel)
                .startTime(codingTestCreateRequest.startTime())
                .endTime(codingTestCreateRequest.endTime())
                .build();

        codingTestRepository.save(codingTest);

        List<Long> problemList =  codingTestCreateRequest
                .problemCreateRequestList()
                .stream()
                .map(problemCreateRequest -> {
                    Problem problem = Problem.builder()
                            .codingTest(codingTest)
                            .problemTitle(problemCreateRequest.problemTitle())
                            .problemUrl(problemCreateRequest.problemUrl())
                            .codingTest(codingTest)
                            .build();
                    return problemRepository.save(problem).getProblemId();
                }).toList();

        return codingTest.getCodingTestId();

    }


    /**
     * 코딩테스트 조회
     *
     * @param codingTestFilterRequest 코딩테스트 관련 정보
     */
    public List<CodingTestResponse> get(CodingTestFilterRequest codingTestFilterRequest) {

        // Specification 생성
        Specification<CodingTest> spec = CodingTestSpecification.filterBy(
            codingTestFilterRequest.codingTestId(),
            codingTestFilterRequest.channelId(),
            codingTestFilterRequest.startTime(),
            codingTestFilterRequest.endTime()
        );

        // Sort sort = Sort.by(Sort.Direction.DESC, "codingTestId");

        // 조건에 맞는 CodingTest 목록 조회
        List<CodingTest> codingTestList = codingTestRepository.findAll(spec);

        // CodingTest -> CodingTestResponse 변환
        return codingTestList.stream()
            .map(codingTest -> CodingTestResponse.builder()
                .codingTestId(codingTest.getCodingTestId())
                .channelId(codingTest.getChannel().getChannelId())
                .startTime(codingTest.getStartTime())
                .endTime(codingTest.getEndTime())
                .build()
            )
            .toList();
    }


    /**
     * 코딩테스트 삭제
     *
     * @param codingTestId 코딩테스트 id
     */
    @Transactional
    public void delete(Long codingTestId,
                       CustomMemberDetails loginMember) throws IllegalAccessException {

        // 1. 코딩 테스트 존재 확인 (권한 확인을 위해 필요함)
        CodingTest codingTest =  codingTestRepository.findById(codingTestId)
                .orElseThrow(() -> new NotFoundException("코딩 테스트가 존재하지 않습니댜"));

        // 2. 권한 확인
        checkOwner(codingTest.getChannel(), loginMember);

        codingTestRepository.delete(codingTest);
    }

    public void checkOwner(Channel channel,
                           CustomMemberDetails loginMember) throws IllegalAccessException {
        Long studyId = channel.getStudy().getStudyId();
        Optional.of(studyParticipantRepository.findOwnerIdByStudyId(studyId))
                .filter(ownerId -> ownerId.equals(loginMember.getMemberId()))
                .orElseThrow(() -> new IllegalAccessException("코딩테스트는 스터디 장만 생성할 수 있습니다."));
    }
}
