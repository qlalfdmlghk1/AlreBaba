package com.ssafy.alrebaba.friend.application;

import com.ssafy.alrebaba.common.storage.application.ImageUtil;
import com.ssafy.alrebaba.friend.domain.Friend;
import com.ssafy.alrebaba.friend.domain.FriendRepository;
import com.ssafy.alrebaba.friend.domain.FriendStatus;
import com.ssafy.alrebaba.friend.dto.request.FriendRequestDto;
import com.ssafy.alrebaba.friend.dto.request.FriendSearchRequestDto;
import com.ssafy.alrebaba.friend.dto.request.FriendStatusUpdateRequestDto;
import com.ssafy.alrebaba.friend.dto.response.*;
import com.ssafy.alrebaba.friend.exception.FriendErrorCode;
import com.ssafy.alrebaba.friend.exception.FriendException;
import com.ssafy.alrebaba.member.domain.Member;
import com.ssafy.alrebaba.member.domain.MemberRepository;
import com.ssafy.alrebaba.member.exception.MemberErrorCode;
import com.ssafy.alrebaba.notification.application.NotificationService;
import com.ssafy.alrebaba.notification.domain.NotificationType;
import com.ssafy.alrebaba.notification.dto.request.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final ImageUtil imageUtil; // pre-signed URL 변환을 위한 ImageUtil

    /**
     * 친구 요청 보내기
     * - 현재 로그인한 회원(memberId)이 대상 회원(requestDto.acceptId())에게 친구 요청을 보냅니다.
     * - 이미 요청이나 친구 관계가 존재하는지 검증하고, 존재하지 않으면 친구 요청을 생성합니다.
     * - 요청 생성 후 알림을 전송합니다.
     */
    @Transactional
    public FriendRequestResponseDto sendFriendRequest(Long memberId, FriendRequestDto requestDto) {
        log.info("sendFriendRequest called for memberId: {} with acceptId: {}", memberId, requestDto.acceptId());
        if (memberId.equals(requestDto.acceptId())) {
            throw new FriendException.FriendConflictException(
                    FriendErrorCode.INVALID_FRIEND_REQUEST,
                    "자기 자신에게 친구 요청을 보낼 수 없습니다."
            );
        }

        Member requestMember = getMemberById(memberId);
        Member acceptMember = getMemberById(requestDto.acceptId());

        boolean exists = friendRepository.existsByRequestMemberAndAcceptMember(requestMember, acceptMember) ||
                friendRepository.existsByRequestMemberAndAcceptMember(acceptMember, requestMember);
        if (exists) {
            throw new FriendException.FriendConflictException(
                    FriendErrorCode.FRIEND_ALREADY_EXISTS,
                    "이미 친구 요청 또는 친구 관계가 존재합니다."
            );
        }

        Friend friend = Friend.builder()
                .requestMember(requestMember)
                .acceptMember(acceptMember)
                .status(FriendStatus.REQUESTED)
                .build();
        friendRepository.save(friend);
        log.info("Friend request created: {} -> {}", requestMember.getMemberId(), acceptMember.getMemberId());

        // 친구 초대 알림 전송
        notificationService.createNotification(
                NotificationRequest.builder()
                        .receiverId(acceptMember.getMemberId())
                        .type(NotificationType.FRIEND_INVITATION)
                        .referenceId(requestMember.getMemberId())
                        .build()
        );

        return new FriendRequestResponseDto(
                memberId,
                requestDto.acceptId(),
                FriendStatus.REQUESTED.name(),
                friend.getCreatedAt()
        );
    }

    /**
     * 친구 목록 조회
     * - 주어진 회원(memberId)이 친구 관계에 참여한 친구 중 상태가 FOLLOWING인 목록을
     *   createdAt 내림차순으로 페이징 조회합니다.
     */
    @Transactional(readOnly = true)
    public FriendListResponseDto getFriendList(Long memberId, Long lastId, int pageSize) {
        log.info("getFriendList called for memberId: {}, lastId: {}, pageSize: {}", memberId, lastId, pageSize);
        LocalDateTime lastCreatedAt = null;
        if (lastId != null && lastId > 0) {
            lastCreatedAt = Instant.ofEpochMilli(lastId).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        List<Friend> friendList = friendRepository.findFriendListByMemberAndStatus(memberId, FriendStatus.FOLLOWING, lastCreatedAt, pageSize);
        return buildFriendListResponse(friendList, pageSize, memberId);
    }

    /**
     * 차단된 친구 목록 조회
     * - 현재 로그인한 회원이 차단한 친구 목록(상태가 BANNED인 경우)을 페이징 조회합니다.
     * - 차단을 실행한 회원은 항상 requestMember이므로 requestMember 기준으로 조회합니다.
     */
    @Transactional(readOnly = true)
    public FriendListResponseDto getBlockedFriendList(Long memberId, Long lastId, int pageSize) {
        log.info("getBlockedFriendList called for memberId: {}, lastId: {}, pageSize: {}", memberId, lastId, pageSize);
        LocalDateTime lastCreatedAt = null;
        if (lastId != null && lastId > 0) {
            lastCreatedAt = Instant.ofEpochMilli(lastId).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        Member currentMember = getMemberById(memberId);
        PageRequest pageable;
        List<Friend> blockedList;
        if (lastCreatedAt == null) {
            pageable = PageRequest.of(0, pageSize + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
            blockedList = friendRepository.findByRequestMemberAndStatusOrderByCreatedAtDesc(currentMember, FriendStatus.BANNED, pageable);
        } else {
            pageable = PageRequest.of(0, pageSize + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
            blockedList = friendRepository.findByRequestMemberAndStatusAndCreatedAtLessThanOrderByCreatedAtDesc(currentMember, FriendStatus.BANNED, lastCreatedAt, pageable);
        }
        return buildFriendListResponse(blockedList, pageSize, memberId);
    }

    /**
     * 받은 친구 요청 목록 조회
     * - 현재 로그인한 회원이 받은 친구 요청(상태가 REQUESTED인 경우)을 페이징 조회합니다.
     */
    @Transactional(readOnly = true)
    public FriendListResponseDto getReceivedFriendRequests(Long memberId, Long lastId, int pageSize) {
        log.info("getReceivedFriendRequests called for memberId: {}, lastId: {}, pageSize: {}", memberId, lastId, pageSize);
        Member member = getMemberById(memberId);
        PageRequest pageRequest = PageRequest.of(0, pageSize + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        LocalDateTime lastCreatedAt = null;
        if (lastId != null && lastId > 0) {
            lastCreatedAt = Instant.ofEpochMilli(lastId).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        List<Friend> friendRequests;
        if (lastCreatedAt == null) {
            friendRequests = friendRepository.findByAcceptMemberAndStatusOrderByRequestMemberMemberIdDesc(
                    member, FriendStatus.REQUESTED, pageRequest
            );
        } else {
            friendRequests = friendRepository.findByAcceptMemberAndStatusAndRequestMemberMemberIdLessThanOrderByRequestMemberMemberIdDesc(
                    member, FriendStatus.REQUESTED, lastId, pageRequest
            );
        }
        boolean hasNext = friendRequests.size() == pageSize + 1;
        if (hasNext) {
            friendRequests.remove(pageSize);
        }
        Long newLastId = friendRequests.isEmpty() ? null :
                friendRequests.get(friendRequests.size() - 1).getCreatedAt()
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        // 각 요청에 대해 pre-signed URL 적용
        List<FriendResponseDto> dtos = friendRequests.stream()
                .map(friend -> FriendResponseDto.from(friend, false, imageUtil))
                .collect(Collectors.toList());
        return FriendListResponseDto.builder()
                .content(dtos)
                .hasNext(hasNext)
                .lastId(newLastId)
                .build();
    }

    /**
     * 보낸 친구 요청 목록 조회 (NoOffset 페이징)
     * - 현재 로그인한 회원이 보낸 친구 요청(상태가 REQUESTED인 경우)을 생성시간(createdAt) 기준으로 페이징 조회합니다.
     */
    @Transactional(readOnly = true)
    public SentFriendRequestsResponseDto getSentFriendRequestsNoOffset(Long memberId, Long lastId, int pageSize) {
        log.info("getSentFriendRequestsNoOffset called for memberId: {}, lastId: {}, pageSize: {}", memberId, lastId, pageSize);
        Member member = getMemberById(memberId);
        PageRequest pageRequest = PageRequest.of(0, pageSize + 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        LocalDateTime lastCreatedAt = null;
        if (lastId != null && lastId > 0) {
            lastCreatedAt = Instant.ofEpochMilli(lastId).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        List<Friend> sentRequests;
        if (lastCreatedAt == null) {
            sentRequests = friendRepository.findByRequestMemberAndStatusOrderByCreatedAtDesc(
                    member, FriendStatus.REQUESTED, pageRequest
            );
        } else {
            sentRequests = friendRepository.findByRequestMemberAndStatusAndCreatedAtLessThanOrderByCreatedAtDesc(
                    member, FriendStatus.REQUESTED, lastCreatedAt, pageRequest
            );
        }
        boolean hasNext = sentRequests.size() == pageSize + 1;
        if (hasNext) {
            sentRequests.remove(pageSize);
        }
        Long newLastId = sentRequests.isEmpty() ? null :
                sentRequests.get(sentRequests.size() - 1).getCreatedAt()
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<FriendResponseDto> dtos = sentRequests.stream()
                .map(friend -> FriendResponseDto.from(friend, true, imageUtil))
                .collect(Collectors.toList());
        return SentFriendRequestsResponseDto.builder()
                .content(dtos)
                .hasNext(hasNext)
                .lastId(newLastId)
                .build();
    }

    /**
     * 친구 검색
     * - 현재 로그인한 회원(currentMemberId)을 기준으로 친구 검색을 수행합니다.
     * - 검색어(searchKeyword)를 포함하는 회원 목록을 페이징 조회하며,
     *   차단된 회원 및 자기 자신은 검색 결과에서 제외합니다.
     */
    @Transactional(readOnly = true)
    public FriendSearchResponseDto searchFriends(Long currentMemberId, FriendSearchRequestDto requestDto) {
        log.info("searchFriends called with search: {}, lastId: {}, pageSize: {}",
                requestDto.search(), requestDto.lastId(), requestDto.pageSize());

        String searchKeyword = requestDto.search();
        int pageSize = requestDto.pageSize() != null && requestDto.pageSize() > 0 ? requestDto.pageSize() : 20;
        Long lastId = requestDto.lastId();
        PageRequest pageable = PageRequest.of(0, pageSize + 1);
        List<Member> members;

        if (lastId == null) {
            members = memberRepository.searchByUniqueIdWithExactFirst(searchKeyword, pageable);
        } else {
            members = memberRepository.searchByUniqueIdWithExactFirstAndMemberIdLessThan(searchKeyword, lastId, pageable);
        }

        // 현재 로그인 회원 조회
        Member currentMember = getMemberById(currentMemberId);

        // 현재 회원과 관련된 모든 Friend 관계 조회 (요청자 또는 수락자로)
        List<Friend> friendRelations = friendRepository.findByRequestMemberOrAcceptMember(currentMember, currentMember);

        // 각 Friend 관계에 대해, 상대방 MemberId를 key로 매핑
        Map<Long, Friend> friendMap = friendRelations.stream().collect(Collectors.toMap(
                friend -> friend.getRequestMember().getMemberId().equals(currentMember.getMemberId())
                        ? friend.getAcceptMember().getMemberId()
                        : friend.getRequestMember().getMemberId(),
                friend -> friend,
                (existing, replacement) -> existing
        ));

        List<FriendResponseDto> dtos = members.stream()
                // 자기 자신은 제외
                .filter(member -> !member.getMemberId().equals(currentMemberId))
                // Friend 관계가 있고, 상태가 BANNED인 경우 내가 차단한 경우(즉, 내가 requestMember인 경우)만 포함
                .filter(member -> {
                    Friend friend = friendMap.get(member.getMemberId());
                    if (friend != null && friend.getStatus() == FriendStatus.BANNED &&
                            !friend.getRequestMember().getMemberId().equals(currentMemberId)) {
                        return false;
                    }
                    return true;
                })
                .map(member -> {
                    Friend friend = friendMap.get(member.getMemberId());
                    // Friend 관계가 있을 경우 Friend의 상태, 없으면 null
                    String friendStatus = friend != null ? friend.getStatus().name() : null;
                    // 기존 Member 엔티티의 status
                    String memberStatus = member.getStatus().name();
                    return FriendResponseDto.builder()
                            .memberId(member.getMemberId())
                            .nickname(member.getNickname())
                            .profileImage(imageUtil.getPreSignedUrl(member.getProfileImage()))
                            .uniqueId(member.getUniqueId())
                            // 기존 Member의 상태
                            .memberStatus(memberStatus)
                            // 존재하는 Friend 관계의 상태 (없으면 null)
                            .friendStatus(friendStatus)
                            .build();
                })
                .collect(Collectors.toList());

        boolean hasNext = dtos.size() == pageSize + 1;
        if (hasNext) {
            dtos = dtos.subList(0, pageSize);
        }
        Long newLastId = dtos.isEmpty() ? null : dtos.get(dtos.size() - 1).memberId();

        return FriendSearchResponseDto.builder()
                .content(dtos)
                .hasNext(hasNext)
                .lastId(newLastId)
                .build();
    }


    /**
     * 친구 상태 업데이트 (예: 요청 수락, 차단 등)
     * - 현재 로그인한 회원(memberId)이 친구(friendId)의 상태를 변경합니다.
     * - 차단(BANNED) 상태의 경우, 현재 회원이 차단을 수행한 측이 되도록 요청자와 수락자의 역할을 스왑합니다.
     * - 상태 변경 후, 필요에 따라 알림을 삭제합니다.
     */
    @Transactional
    public FriendRequestResponseDto updateFriendStatus(Long memberId, FriendStatusUpdateRequestDto requestDto, Long notificationId) {
        log.info("updateFriendStatus called for memberId: {}, friendId: {}", memberId, requestDto.friendId());
        Member currentMember = getMemberById(memberId);
        Member friendMember = getMemberById(requestDto.friendId());

        // 양쪽 방향에서 친구 관계를 조회합니다.
        Optional<Friend> friendOptional = friendRepository.findByRequestMemberAndAcceptMember(currentMember, friendMember);
        if (!friendOptional.isPresent()) {
            friendOptional = friendRepository.findByRequestMemberAndAcceptMember(friendMember, currentMember);
        }
        Friend friend = friendOptional.orElseThrow(() ->
                new RuntimeException(FriendErrorCode.FRIEND_NOT_FOUND.getMessage())
        );

        FriendStatus newStatus = FriendStatus.valueOf(requestDto.status());

        if (newStatus == FriendStatus.BANNED) {
            // 현재 사용자가 이미 requestMember라면 그대로 업데이트
            if (!friend.getRequestMember().getMemberId().equals(currentMember.getMemberId())) {
                // 현재 사용자가 acceptMember인 경우 -> 스왑 로직 적용 필요
                // 복합키 변경이 안되므로, 기존 엔티티를 삭제하고 새 엔티티를 생성합니다.

                // 1. 기존 엔티티 삭제
                friendRepository.delete(friend);

                // 2. 새 엔티티 생성: 현재 사용자가 requestMember가 되고, 기존 requestMember가 acceptMember가 됩니다.
                Friend newFriend = Friend.builder()
                        .requestMember(currentMember)
                        .acceptMember(friend.getRequestMember())
                        .status(newStatus)
                        // createdAt은 새로 생성되거나, 필요시 기존 값으로 설정할 수 있음
                        .build();
                friendRepository.save(newFriend);
                friend = newFriend;
            } else {
                // 이미 현재 사용자가 requestMember이면, 단순히 상태만 변경
                friend.setStatus(newStatus);
            }
        } else {
            // BANNED가 아닌 경우 단순 업데이트
            friend.setStatus(newStatus);
        }

        log.info("updateFriendStatus updated status to {}", friend.getStatus().name());

        // notificationId가 제공되면 해당 알림 삭제
        if (notificationId == null) {
            notificationId = notificationService.findNotificationId(NotificationType.FRIEND_INVITATION, memberId, friendMember.getMemberId());
        }
        log.info("notification ID: {}", notificationId);
        notificationService.deleteNotificationById(notificationId);

        return new FriendRequestResponseDto(
                friend.getRequestMember().getMemberId(),
                friend.getAcceptMember().getMemberId(),
                friend.getStatus().name(),
                friend.getCreatedAt()
        );
    }


    /**
     * 친구 삭제
     * - 현재 로그인한 회원(memberId)과 대상 친구(friendId)의 관계가 존재하면 삭제합니다.
     * - 존재하지 않는 경우에도 idempotent하게 동작합니다.
     */
    @Transactional
    public void deleteFriend(Long memberId, Long friendId) {
        log.info("deleteFriend called for memberId: {}, friendId: {}", memberId, friendId);
        Member currentMember = getMemberById(memberId);
        Member friendMember = getMemberById(friendId);
        Optional<Friend> friendOptional = friendRepository.findByRequestMemberAndAcceptMember(currentMember, friendMember);
        if (!friendOptional.isPresent()) {
            friendOptional = friendRepository.findByRequestMemberAndAcceptMember(friendMember, currentMember);
        }
        friendOptional.ifPresent(friend -> {
            friendRepository.delete(friend);
            log.info("deleteFriend succeeded for friend relationship between {} and {}", memberId, friendId);
        });
    }

    /**
     * 공통 응답 빌더
     * - 주어진 Friend 목록에 대해 pre-signed URL을 적용하고, 페이징 정보(lastId, hasNext)를 포함하는 응답 객체를 생성합니다.
     */
    private FriendListResponseDto buildFriendListResponse(List<Friend> friendList, int pageSize, Long memberId) {
        boolean hasNext = friendList.size() > pageSize;
        if (hasNext) {
            friendList = friendList.subList(0, pageSize);
        }
        Long newLastId = friendList.isEmpty() ? null :
                friendList.get(friendList.size() - 1).getCreatedAt()
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<FriendResponseDto> dtos = friendList.stream()
                .map(friend -> {
                    boolean isRequester = friend.getRequestMember().getMemberId().equals(memberId);
                    return FriendResponseDto.from(friend, isRequester, imageUtil);
                })
                .collect(Collectors.toList());
        return FriendListResponseDto.builder()
                .content(dtos)
                .hasNext(hasNext)
                .lastId(newLastId)
                .build();
    }

    /**
     * 현재 로그인한 회원 조회
     * - 주어진 memberId에 해당하는 회원이 존재하면 반환하고, 존재하지 않으면 예외를 발생시킵니다.
     */
    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()));
    }
}
