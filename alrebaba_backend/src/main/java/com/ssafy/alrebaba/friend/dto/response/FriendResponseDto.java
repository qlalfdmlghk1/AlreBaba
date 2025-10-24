package com.ssafy.alrebaba.friend.dto.response;

import com.ssafy.alrebaba.common.storage.application.ImageUtil;
import com.ssafy.alrebaba.friend.domain.Friend;
import com.ssafy.alrebaba.member.domain.Member;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

/**
 * 친구 응답 DTO
 */
@Builder
public record FriendResponseDto(
        Long memberId,
        String nickname,
        String profileImage,
        String memberStatus, // Member 엔티티의 상태
        String friendStatus, // Friend 관계의 상태 (관계가 없으면 null)
        String uniqueId,
        String createdAt    // 년-월-일 포맷 (yyyy-MM-dd)
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Friend 엔티티를 기반으로, 요청자 여부에 따라 상대방 정보를 추출하여 DTO를 생성합니다.
     *
     * @param friend       친구 엔티티
     * @param isRequester  현재 사용자가 요청자일 경우 true, 아니면 false
     * @param imageUtil    pre-signed URL 생성을 위한 유틸리티
     * @return 변환된 FriendResponseDto 객체
     */
    public static FriendResponseDto from(Friend friend, boolean isRequester, ImageUtil imageUtil) {
        if (friend == null) {
            throw new IllegalArgumentException("Friend 객체가 null입니다.");
        }
        // 요청자 여부에 따라 상대방의 Member 엔티티를 선택합니다.
        Member otherMember = isRequester ? friend.getAcceptMember() : friend.getRequestMember();
        String rawProfileImage = otherMember.getProfileImage();
        String profileImage = imageUtil.getPreSignedUrl(rawProfileImage);

        return FriendResponseDto.builder()
                .memberId(otherMember.getMemberId())
                .nickname(otherMember.getNickname())
                .profileImage(profileImage)
                .memberStatus(otherMember.getStatus().name()) // Member의 원래 상태
                .friendStatus(friend.getStatus().name())       // Friend 관계의 상태
                .uniqueId(otherMember.getUniqueId())
                .createdAt(otherMember.getCreatedAt().format(FORMATTER))
                .build();
    }

    /**
     * Friend 관계가 없는 경우, Member 정보만으로 DTO를 생성할 수 있도록 합니다.
     *
     * @param member     Member 엔티티
     * @param imageUtil  pre-signed URL 생성을 위한 유틸리티
     * @return 변환된 FriendResponseDto 객체 (friendStatus는 null)
     */
    public static FriendResponseDto from(Member member, ImageUtil imageUtil) {
        if (member == null) {
            throw new IllegalArgumentException("Member 객체가 null입니다.");
        }
        String profileImage = imageUtil.getPreSignedUrl(member.getProfileImage());
        return FriendResponseDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profileImage(profileImage)
                .memberStatus(member.getStatus().name())
                .friendStatus(null)  // Friend 관계가 없으므로 null 처리
                .uniqueId(member.getUniqueId())
                .createdAt(member.getCreatedAt().format(FORMATTER))
                .build();
    }
}
