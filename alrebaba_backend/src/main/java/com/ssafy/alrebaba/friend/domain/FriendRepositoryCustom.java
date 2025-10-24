package com.ssafy.alrebaba.friend.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface FriendRepositoryCustom {
    /**
     * 특정 회원(memberId)이 친구 관계에 참여한 (요청자 또는 수락자) 경우를 대상으로,
     * 지정된 상태(status)를 갖는 친구 목록을 createdAt 내림차순으로 조회합니다.
     * lastCreatedAt이 null이면 첫 페이지, 그렇지 않으면 해당 시각보다 이전의 데이터만 조회합니다.
     * 최대 (pageSize + 1) 건을 조회하여 다음 페이지 존재 여부를 판단합니다.
     *
     * @param memberId      조회 대상 회원의 ID
     * @param status        조회할 친구 상태 (예: FOLLOWING, BANNED 등)
     * @param lastCreatedAt 마지막 조회된 createdAt (첫 페이지는 null)
     * @param pageSize      한 페이지당 조회할 건수
     * @return              조회된 Friend 목록 (최대 pageSize+1 건)
     */
    List<Friend> findFriendListByMemberAndStatus(Long memberId, FriendStatus status, LocalDateTime lastCreatedAt, int pageSize);
}
