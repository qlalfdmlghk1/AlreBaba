package com.ssafy.alrebaba.code.application;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.ssafy.alrebaba.channel.domain.Channel;
import com.ssafy.alrebaba.channel.domain.ChannelRepository;
import com.ssafy.alrebaba.code.domain.Code;
import com.ssafy.alrebaba.code.domain.CodeRepository;
import com.ssafy.alrebaba.code.domain.CodeSpecification;
import com.ssafy.alrebaba.code.domain.Language;
import com.ssafy.alrebaba.code.domain.Platform;
import com.ssafy.alrebaba.code.dto.request.CRDTMessage;
import com.ssafy.alrebaba.code.dto.request.CodeCreateRequest;
import com.ssafy.alrebaba.code.dto.request.CodeFilterRequest;
import com.ssafy.alrebaba.code.dto.request.CodePatchRequest;
import com.ssafy.alrebaba.code.dto.response.CodePageResponse;
import com.ssafy.alrebaba.code.dto.response.CodeResponse;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.problem.domain.Problem;
import com.ssafy.alrebaba.problem.domain.ProblemRepository;
import com.ssafy.alrebaba.study.domain.Study;
import com.ssafy.alrebaba.study.domain.participant.StudyParticipantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeService {

	private static final String CODE_TOPIC = "code-messages";

	private final KafkaTemplate<String, CRDTMessage> kafkaTemplate;
	private final CodeRepository codeRepository;
	private final ChannelRepository channelRepository;
	private final StudyParticipantRepository studyParticipantRepository;
	private final ProblemRepository problemRepository;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 코드 생성
	 *
	 * @param codeCreateRequest 로그인한 회원의 ID
	 * @param channelId 스터디 이름, 이미지를 포함한 request
	 * @param loginMember 스터디 이름, 이미지를 포함한 request
	 */
	@Transactional
	public Long create(Long channelId,
						CodeCreateRequest codeCreateRequest,
						CustomMemberDetails loginMember) throws BadRequestException {

		// 1. channel 존재 여부 확인
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(()-> new NotFoundException("채널을 찾을 수 없습니다."));

		// 2. problem에 문제가 이미 있는 지 확인
		Problem problem = null;
		if(codeCreateRequest.problemId() != null){
			problem = problemRepository.findById(codeCreateRequest.problemId())
					.orElseThrow(() -> new NotFoundException("문제를 찾을 수 없습니다."));
		}
		if(codeCreateRequest.problemId() != null
				&& codeRepository.existsByProblemIdAndMemberId(codeCreateRequest.problemId(), loginMember.getMemberId())){
			throw new BadRequestException("이미 이 문제에 대한 코드가 존재합니다.");
		}

		Code code = Code.builder()
			.platform(Platform.fromString(codeCreateRequest.platform()))
			.title(codeCreateRequest.title())
			.context(codeCreateRequest.context())
			.channel(channel)
			.language(codeCreateRequest.language() == null ? Language.PLAINTEXT : Language.fromString(codeCreateRequest.language()))
			.member(loginMember.getMember())
				.problem(problem)
			.build();

		codeRepository.save(code);

		return code.getCodeId();
	}

	/**
	 * 코드 검색
	 *
	 * @param crdtMessage 코드를 보내기 위한 메세지 객체
	 */
	public void sendCRDT(CRDTMessage crdtMessage){
		kafkaTemplate.send(CODE_TOPIC, crdtMessage);
		messagingTemplate.convertAndSend("/topic/code/" + crdtMessage.channelId(), crdtMessage);
	}

	public void sendSnapShot(CRDTMessage crdtMessage){;
		messagingTemplate.convertAndSendToUser(crdtMessage.sessionId(),"/topic/snapshot/"+crdtMessage.channelId(), crdtMessage );
	}


	/**
	 * 코드 검색
	 *
	 * @param codeFilterRequest 코드를 검색하기 위한 조건들
	 */
	public CodePageResponse get(CodeFilterRequest codeFilterRequest){

		Specification<Code> spec = CodeSpecification.filterBy(
				codeFilterRequest.codeId(),
				codeFilterRequest.channelId(),
				codeFilterRequest.platform(),
				codeFilterRequest.title(),
				codeFilterRequest.language(),
				codeFilterRequest.memberId(),
				codeFilterRequest.problemId(),
				codeFilterRequest.lastId());
		int count = codeFilterRequest.count() == null ? 10 : codeFilterRequest.count();

		Pageable pageable = (Pageable) PageRequest.of(0,
				count + 1,
				Sort.by(Sort.Direction.DESC, "codeId"));

		Page<Code> pageCode = codeRepository.findAll(spec, pageable);

		List<CodeResponse> codeResponseList = pageCode.stream()
				.map(page -> CodeResponse.builder()
						.codeId(page.getCodeId())
						.channelId(Optional.ofNullable(page.getChannel()).map(Channel::getChannelId).orElse(null))
						.platform(page.getPlatform().toString())
						.title(page.getTitle())
						.language(page.getLanguage().toString())
						.createAt(page.getCreateAt())
						.memberId(page.getMember().getMemberId())
						.problemId(Optional.ofNullable(page.getProblem()).map(Problem::getProblemId).orElse(null))
						.build())
				.toList();

		boolean hasNext = codeResponseList.size() > count;
		int lastIndex = hasNext ? codeResponseList.size() -2 : codeResponseList.size() -1;

		List<CodeResponse> limitedCodeResponseList = codeResponseList.isEmpty() ?
				List.of() : codeResponseList.subList(0, lastIndex+1);

		return CodePageResponse.builder()
				.content(limitedCodeResponseList)
				.hasNext(hasNext)
				.lastId(limitedCodeResponseList.isEmpty() ? null : limitedCodeResponseList.get(lastIndex).codeId())
				.build();

	}

	public String getDetail(Long codeId){
		Code code = codeRepository.findById(codeId)
				.orElseThrow(() -> new NotFoundException("코드가 존재하지 않습니다."));
		return code.getContext();
	}

	/**
	 * 코드 정보 수정
	 *
	 * @param codePatchRequest 코드 정보를 수정하기 위한 정보
	 * @param loginMember 로그인 한 member 정보
	 */
	@Transactional
	public void patch(CodePatchRequest codePatchRequest,
					  CustomMemberDetails loginMember) throws AccessDeniedException {
		// 1. code 검색
		Code code = codeRepository.findById(codePatchRequest.codeId())
				.orElseThrow(() -> new NotFoundException("코드를 찾을 수 없습니다."));

		Study study  = code.getChannel().getStudy();

		// 2. 스터디 참여자 인지 확인
		List<Long> participantIdList = studyParticipantRepository.findJoinedMemberIdsByStudyId(study.getStudyId());
		if(!participantIdList.contains(loginMember.getMemberId())){
			throw new AccessDeniedException("수정 권한이 없습니다.");
		}

		// 3. 업데이트
		if(codePatchRequest.platform() != null)
			code.setPlatform(Platform.fromString(codePatchRequest.platform()));
		if(codePatchRequest.title() != null)
			code.setTitle(codePatchRequest.title());
		if(codePatchRequest.context() != null)
			code.setContext(codePatchRequest.context());
		if(codePatchRequest.language() != null)
			code.setLanguage(Language.fromString(codePatchRequest.language()));

	}


}
