package com.ssafy.alrebaba.study.application;

import com.ssafy.alrebaba.channel.application.ChannelService;
import com.ssafy.alrebaba.channel.dto.request.ChannelCreateRequest;
import com.ssafy.alrebaba.channel.dto.response.ChannelResponse;
import com.ssafy.alrebaba.common.exception.ForbiddenException;
import com.ssafy.alrebaba.common.exception.NotFoundException;
import com.ssafy.alrebaba.common.storage.application.ImageUtil;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.member.dto.response.MemberInfoListByStatus;
import com.ssafy.alrebaba.study.domain.Study;
import com.ssafy.alrebaba.study.domain.StudyRepository;
import com.ssafy.alrebaba.study.dto.request.StudyCreateRequest;
import com.ssafy.alrebaba.study.dto.request.StudyUpdateRequest;
import com.ssafy.alrebaba.study.dto.response.StudyListResponse;
import com.ssafy.alrebaba.study.dto.response.StudyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyParticipantService studyParticipantService;
    private final ChannelService channelService;
    private final ImageUtil imageUtil;
    // TODO: Service에서 다른 Service 참조해도 될까?

    @Value("${basic.image.study-url}")
    private String basicStudyImageUrl; // 기본 프로필 이미지 URL 설정

    /**
     * 스터디 생성
     *
     * @param loginId 로그인한 회원의 ID
     * @param request 스터디 이름, 이미지를 포함한 request
     */
    @Transactional
    public Long create(Long loginId, StudyCreateRequest request) {
        // 1. 스터디 생성
        Study study = Study.builder()
                .studyName(request.studyName())
                .imageUrl(basicStudyImageUrl)
                .build();

        studyRepository.save(study);

        // 2. 스터디 생성자(OWNER) 등록
        studyParticipantService.registerParticipant(
                study.getStudyId(),
                loginId,
                loginId
        );

        // 3. 기본 채널(회의실, 소통) 생성
        List.of(
                new ChannelCreateRequest("회의실", "MEETING"),
                new ChannelCreateRequest("소통", "CHAT")
        ).forEach(channelRequest -> channelService.create(study.getStudyId(), loginId, channelRequest));

        return study.getStudyId();
    }

    /**
     * 스터디 상세 조회
     *
     * @param studyId 스터디 ID
     * @return 스터디 상세 정보
     */
    @Transactional(readOnly = true)
    public StudyResponse get(Long studyId) {
        // 1. 스터디 조회 (존재하지 않으면 예외 발생)
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundException("스터디가 존재하지 않습니다."));

        // 2. 스터디 방 생성자 조회
        Long ownerId = studyParticipantService.getStudyOwnerId(studyId);

        // 3. 스터디 참가자 목록 조회 (참가자 status는 JOINED)
        List<MemberInfoListByStatus> participants = studyParticipantService.getParticipants(studyId);

        // 4. 스터디 채널 목록 조회
        List<ChannelResponse> channels = channelService.findAllByStudy(study.getStudyId());

        // 5. StudyResponse 생성 및 반환
        return StudyResponse.of(study, ownerId, participants, channels);
    }

    /**
     * 로그인 한 멤버가 참여한 스터디 목록 조회
     *
     * @param loginMember 스터디 ID
     * @return 스터디 목록
     */
    @Transactional(readOnly = true)
    public List<StudyListResponse> getList(CustomMemberDetails loginMember){
        List<Study> studyList = studyRepository.findStudyByMemberId(loginMember.getMemberId());

        return studyList.stream()
                .map(study -> StudyListResponse.builder()
                        .studyId(study.getStudyId())
                        .imageUrl(imageUtil.getPreSignedUrl(study.getImageUrl()))
                        .build())
                .toList();
    }

    /**
     * 스터디 이미지 업로드
     *
     * @param studyId 스터디 ID
     * @param multipartFile 스터디 이미지
     * @param loginMember 로그인
     */
    @Transactional
    public String uploadStudyImage(Long studyId,
                                 MultipartFile multipartFile,
                                 CustomMemberDetails loginMember) throws IOException {

        // 1. 스터디 조회 (존재하지 않으면 예외 발생)
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundException("스터디가 존재하지 않습니다."));

        // 2. 스터디 방 생성자 조회
        Long ownerId = (studyParticipantService.getStudyOwnerId(studyId));
        if(!Objects.equals(ownerId, loginMember.getMemberId())){
            throw new AccessDeniedException("권한이 없습니다.");
        }

        // 3. 이미지 업로드
        String url = imageUtil.store(multipartFile,"study");
        study.setImageUrl(url);
        return url;
    }

    /**
     * 스터디 수정
     *
     * @param studyId 스터디 ID
     * @param loginId 로그인한 사용자 ID
     * @param request 스터디 수정 요청 데이터
     */
    @Transactional
    public void update(Long studyId, Long loginId, StudyUpdateRequest request) {

        // 1. 스터디 조회 (존재하지 않으면 예외 발생)
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundException("스터디가 존재하지 않습니다."));

        // 2. 스터디 요청자가 호스트(스터디 생성자)인지 확인
        Long ownerId = studyParticipantService.getStudyOwnerId(studyId);
        if(!Objects.equals(ownerId, loginId)){
            throw new ForbiddenException("스터디 수정 권한이 없습니다.");
        }

        // 3. 스터디 업데이트 (null이 아닌 필드만 업데이트)
        if (request.studyName() != null) {
            study.updateStudyName(request.studyName());
        }

        if (request.description() != null) {
            study.updateDescription(request.description());
        }
    }

    /**
     * 스터디 삭제
     *
     * @param studyId 스터디 ID
     * @param loginMember 로그인한 사용자
     */
    @Transactional
    public void delete(Long studyId,
                       CustomMemberDetails loginMember){

        // 1. 스터디 조회 (존재하지 않으면 예외 발생)
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundException("스터디가 존재하지 않습니다."));

        // 2. 스터디 요청자가 호스트(스터디 생성자)인지 확인
        Long ownerId = studyParticipantService.getStudyOwnerId(studyId);
        if(!Objects.equals(ownerId, loginMember.getMemberId())){
            throw new ForbiddenException("스터디 삭제 권한이 없습니다.");
        }

        studyRepository.delete(study);
    }

}
