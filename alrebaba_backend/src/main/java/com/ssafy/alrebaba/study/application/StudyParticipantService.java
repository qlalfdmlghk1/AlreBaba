package com.ssafy.alrebaba.study.application;

import com.ssafy.alrebaba.common.exception.BadRequestException;
import com.ssafy.alrebaba.common.exception.NotFoundException;
import com.ssafy.alrebaba.common.storage.application.ImageUtil;
import com.ssafy.alrebaba.friend.domain.FriendRepository;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import com.ssafy.alrebaba.member.dto.response.MemberInfo;
import com.ssafy.alrebaba.member.dto.response.MemberInfoListByStatus;
import com.ssafy.alrebaba.notification.application.NotificationService;
import com.ssafy.alrebaba.notification.domain.NotificationType;
import com.ssafy.alrebaba.notification.dto.request.NotificationRequest;
import com.ssafy.alrebaba.study.domain.Study;
import com.ssafy.alrebaba.study.domain.StudyRepository;
import com.ssafy.alrebaba.study.domain.participant.*;
import com.ssafy.alrebaba.study.dto.response.ParticipantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyParticipantService {

    private final StudyParticipantRepository studyParticipantRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final NotificationService notificationService;
    private final ImageUtil imageUtil;

    /**
     * 참가자 초대 요청
     *
     * @param studyId 스터디 ID
     * @param inviteeId 초대 받은 회원 ID
     * @param inviterId 초대를 요청한 회원 ID
     */
    @Transactional
    public void registerParticipant(Long studyId, Long inviteeId, Long inviterId) {
        // 1. 스터디 & 회원 유효성 검사
        Study study = validateStudy(studyId);
        Member invitee = validateMember(inviteeId);

        // 2. 친구 관계인지 확인 (본인이 초대받는 경우 제외)
        if(!inviterId.equals(inviteeId)) {
            Member inviter = validateMember(inviterId);
            validateFriendship(inviter, invitee);
        }

        // 3. 이미 초대된 참가자인지 확인
        if(studyParticipantRepository.existsById(new StudyParticipantId(studyId, inviteeId))){
            throw new BadRequestException("이미 요청된 초대입니다.");
        }

        // 4. 초대 요청 처리: 본인이 초대 받는 경우 OWNER, 다른 사람인 경우 STUDENT
        ParticipantRole role = (inviterId.equals(inviteeId)) ? ParticipantRole.OWNER : ParticipantRole.STUDENT;
        ParticipantStatus status = (inviterId.equals(inviteeId)) ? ParticipantStatus.JOINED : ParticipantStatus.REQUESTED;


        // 5. 참가자 등록
        StudyParticipant participant = StudyParticipant.builder()
                .study(study) // Study 엔티티 설정
                .member(invitee) // Member 엔티티 설정
                .participantRole(role)
                .participantStatus(status)
                .build();

        studyParticipantRepository.save(participant);

        // 6. 스터디 참가자만 초대 알림 요청
        if(role != ParticipantRole.OWNER) {
            notificationService.createNotification(
                    NotificationRequest.builder()
                            .receiverId(inviteeId)
                            .type(NotificationType.STUDY_JOIN_REQUEST)
                            .referenceId(studyId)
                            .build()
            );
        }
    }

    /**
     * 참가자 초대 수락
     *
     * @param studyId 스터디 ID
     * @param inviteeId 초대 받은 회원 ID
     */
    @Transactional
    public void acceptInvitation(Long studyId, Long inviteeId, Long notificationId) {
        // 1. 스터디 유효성 검사
        validateStudy(studyId);

        // 2. 초대 요청 유효성 검사
        StudyParticipantId participantId = new StudyParticipantId(studyId, inviteeId);
        StudyParticipant participant = studyParticipantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException("초대 요청을 찾을 수 없습니다."));

        // 3. 초대 상태가 REQUESTED인지 확인
        if(participant.getParticipantStatus() != ParticipantStatus.REQUESTED) {
            throw new BadRequestException("수락할 수 없는 상태입니다.");
        }

        // 4. 상태 업데이트 REQUESTED -> JOINED
        participant.updateStatus(ParticipantStatus.JOINED);

        // 5. 알림 삭제 (RequestParam으로 전달받은 notificationId를 사용)
        notificationService.deleteNotificationById(notificationId);
    }

    /**
     * 참가자 초대 거절
     *
     * @param studyId 스터디 ID
     * @param inviteeId 초대 받은 회원 ID
     */
    @Transactional
    public void rejectInvitation(Long studyId, Long inviteeId, Long notificationId) {
        // 1. 스터디 유효성 검사
        validateStudy(studyId);

        // 2. 참가자 요청 유효성 검사
        StudyParticipantId participantId = new StudyParticipantId(studyId, inviteeId);
        StudyParticipant participant = studyParticipantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException("참가자를 찾을 수 없습니다."));

        // 3. 초대 상태가 REQUESTED인지 확인
        if(participant.getParticipantStatus() != ParticipantStatus.REQUESTED) {
            throw new BadRequestException("거절할 수 없는 상태입니다.");
        }

        // 4. 참가자 삭제(거절 처리)
        studyParticipantRepository.delete(participant);

        // 5. 알림 삭제 (RequestParam으로 전달받은 notificationId를 사용)
        notificationService.deleteNotificationById(notificationId);
    }

    /**
     * 스터디 참가자 목록 조회
     *
     * @param studyId 스터디 ID
     * @return 상태별 참가자 목록 (닉네임, 프로필 이미지, 상태 포함)
     */
    @Transactional(readOnly = true)
    public List<MemberInfoListByStatus> getParticipants(Long studyId) {
        // 1. JOINED 상태의 스터디 참가자 조회
        List<Long> memberIds = studyParticipantRepository.findJoinedMemberIdsByStudyId(studyId);

        // 2. 해당 참가자 ID로 회원 정보 조회
        List<Member> members = memberRepository.findAllById(memberIds);

        // 3. 상태(ex. ONLINE) 별로 분류
        Map<String, List<MemberInfo>> membersByStatus = members.stream()
                .map(member -> MemberInfo.of(member, imageUtil))  // Member와 imageUtil을 인자로 전달
                .collect(Collectors.groupingBy(MemberInfo::status));

        // 4. 상태별 목록 생성
        return membersByStatus.entrySet().stream()
                .map(entry -> MemberInfoListByStatus.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ParticipantResponse getParticipantInfo(Long studyId, Long memberId) {
        // 1. 스터디 & 회원 정보 조회
        validateStudy(studyId);
        Member member = validateMember(memberId);

        // 2. 참가자 요청 유효성 검사
        StudyParticipantId participantId = new StudyParticipantId(studyId, memberId);
        StudyParticipant participant = studyParticipantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException("참가자를 찾을 수 없습니다."));

        // 3. 스터디 참가자의 Role 반환
        return ParticipantResponse.builder()
                .memberId(memberId)
                .nickname(member.getNickname())
                .role(participant.getParticipantRole().name())
                .build();
    }


    /**
     * 스터디 방 생성자 ID 조회
     *
     * @param studyId 스터디 ID
     */
    @Transactional(readOnly = true)
    public Long getStudyOwnerId(Long studyId) {
        return studyParticipantRepository.findOwnerIdByStudyId(studyId);
    }

    /**
     * 스터디 나가기 또는 호스트가 참가자 내보내기
     *
     * @param studyId 스터디 ID
     * @param loginId 요청한 사용자 ID (직접 나가는 경우 본인, 강퇴하는 경우 호스트)
     * @param targetId 대상 사용자 ID (직접 나가기면 본인, 강퇴라면 대상 참가자)
     */
    @Transactional
    public void exitStudy(Long studyId, Long loginId, Long targetId) {
        // 1. 스터디 유효성 검사
        validateStudy(studyId);

        StudyParticipantId requesterParticipantId = new StudyParticipantId(studyId, loginId);
        StudyParticipantId targetParticipantId = new StudyParticipantId(studyId, targetId);

        // 2. 요청자 정보 확인 (호스트 여부 판단을 위해 필요)
        StudyParticipant requester = studyParticipantRepository.findById(requesterParticipantId)
                .orElseThrow(() -> new NotFoundException("요청자를 찾을 수 없습니다."));

        // 3. 대상 참가자 정보 확인
        StudyParticipant targetParticipant = studyParticipantRepository.findById(targetParticipantId)
                .orElseThrow(() -> new NotFoundException("대상 참가자를 찾을 수 없습니다."));

        // 4-A. 참가자가 직접 나가는 경우 (요청자와 대상이 동일한 경우)
        if (loginId.equals(targetId)) {
            if (requester.getParticipantRole() == ParticipantRole.OWNER) {
                throw new BadRequestException("호스트는 스터디를 직접 나갈 수 없습니다. 스터디를 삭제하세요.");
            }
            studyParticipantRepository.delete(targetParticipant);
            return;
        }

        // 4-B. 호스트가 참가자를 내보내는 경우 (요청자와 대상이 다르고, 요청자가 호스트여야 함)
        if (requester.getParticipantRole() != ParticipantRole.OWNER) {
            throw new BadRequestException("참가자를 내보낼 권한이 없습니다.");
        }

        if (targetParticipant.getParticipantRole() == ParticipantRole.OWNER) {
            throw new BadRequestException("호스트는 내보낼 수 없습니다.");
        }

        // 5. 참가자 나가기 or 내보내기
        studyParticipantRepository.delete(targetParticipant);
    }

    private Study validateStudy(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 스터디입니다."));
    }

    private Member validateMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    /**
     * 친구 관계 유효성 검사 (친구가 아니면 예외 발생)
     */
    private void validateFriendship(Member inviter, Member invitee) {
        if (!friendRepository.existsFriendship(inviter, invitee)) {
            throw new BadRequestException("초대하려면 친구 관계여야 합니다.");
        }
    }

}