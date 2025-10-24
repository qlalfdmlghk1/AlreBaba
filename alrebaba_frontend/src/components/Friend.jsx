import React, { useState, useEffect } from "react";
import profile from "../assets/images/profile.png";
import IconBlock from "./Icons/IconBlock";
import IconCancel from "./Icons/IconCancel";
import IconLine from "./Icons/IconLine";
import IconAccept from "./Icons/IconAccept";
import UserStatus from "./UserStatus";
import { acceptOrBlockFriend, deleteFriend } from "../service/friend";
import "./Friend.css";

function Friend({
  friends,
  setFriends,
  selectedMenu,
  selectedfriend,
  setSelectedFriend,
  setIsUserInfoVisible, // 새로운 상태 관리 함수 추가
}) {
  const [isLoading, setIsLoading] = useState(true); // 로딩 상태 추가

  // friends 리스트가 변경될 때까지 로딩 상태 유지
  useEffect(() => {
    const fetchFriends = async () => {
      setIsLoading(true); // 로딩 시작
      await new Promise((resolve) => setTimeout(resolve, 500)); // 0.5초 강제 지연 (API 요청 시 제거 가능)

      setIsLoading(false); // 데이터가 업데이트되면 로딩 해제
    };

    fetchFriends();
  }, [friends]); // friends가 변경될 때마다 실행

  // 친구 요청 수락 (status="FOLLOWING")
  const handleAcceptFriend = async (friendId, event) => {
    event.stopPropagation();
    try {
      console.log(`✅ 친구 요청 수락: ${friendId}`);
      const response = await acceptOrBlockFriend(friendId, "FOLLOWING");

      if (response) {
        console.log("🎉 친구 요청을 수락했습니다!", response);
        alert("🎉 친구 요청을 수락했습니다!");

        // UI에서 즉시 반영 (받은 요청 목록에서 제거)
        setFriends((prevFriends) => {
          if (!prevFriends || !prevFriends.content) return prevFriends;

          return {
            ...prevFriends,
            content: prevFriends.content.filter(
              (friend) => friend.memberId !== friendId
            ),
          };
        });
      } else {
        console.error("❌ 친구 요청 수락 실패");
      }
    } catch (error) {
      console.error("❌ 친구 요청 수락 중 오류 발생:", error);
    }
  };

  // 친구 차단 (status="BANNED")
  const handleBlockFriend = async (friendId, event) => {
    event.stopPropagation();
    try {
      console.log(`⛔ 친구 차단: ${friendId}`);
      const response = await acceptOrBlockFriend(friendId, "BANNED");

      if (response) {
        console.log("🚫 친구를 차단했습니다!", response);
        alert("🚫 친구를 차단했습니다!");

        // UI에서 즉시 반영 (차단된 친구 제거)
        setFriends((prevFriends) => {
          if (!prevFriends || !prevFriends.content) return prevFriends;

          return {
            ...prevFriends,
            content: prevFriends.content.filter(
              (friend) => friend.memberId !== friendId
            ),
          };
        });
      } else {
        console.error("❌ 친구 차단 실패");
      }
    } catch (error) {
      console.error("❌ 친구 차단 중 오류 발생:", error);
    }
  };

  // 친구 삭제
  const handleDeleteFriend = async (friendId, event) => {
    event.stopPropagation();
    try {
      console.log(`🗑 친구 삭제: ${friendId}`);
      if (!confirm("정말 취소하시겠습니까?")) {
        return;
      }
      const response = await deleteFriend(friendId);
      console.log("response", response);

      if (response) {
        console.log("🗑 친구를 삭제했습니다!", response);

        // friends가 객체인지 배열인지 확인 후 업데이트
        setFriends((prevFriends) => {
          if (!prevFriends) return prevFriends;

          // 객체 형태일 경우 (prevFriends.content가 존재할 때)
          if (prevFriends.content) {
            return {
              ...prevFriends,
              content: prevFriends.content.filter(
                (friend) => friend.memberId !== friendId
              ),
            };
          }

          // 배열 형태일 경우 (prevFriends가 content 없이 배열일 때)
          return prevFriends.filter((friend) => friend.memberId !== friendId);
        });
      } else {
        console.error("❌ 친구 삭제 실패");
      }
    } catch (error) {
      console.error("❌ 친구 삭제 중 오류 발생:", error);
    }
  };

  // 친구 선택시 selectedFriend가 이전 값과 동일하면 setIsUserInfoVisible(false) 호출
  const handleSelectFriend = (friend) => {
    if (friend === selectedfriend) {
      setIsUserInfoVisible(false);
    } else {
      setSelectedFriend(friend);
      setIsUserInfoVisible(true);
    }
  };

  return (
    <div className="friend">
      <ul className="friend-list">
        {isLoading ? (
          <p className="loading-message">로딩 중...</p>
        ) : friends && friends.length > 0 ? (
          friends.map((friend) => (
            <div
              key={friend.memberId}
              onClick={() => handleSelectFriend(friend)}
            >
              <li className="friend-list-infos">
                <div className="friend-list-detail">
                  <div className="user-image-container">
                    <img
                      src={friend.profileImage || profile}
                      alt="user image"
                    />
                    <div className="friend-list-detail-status">
                      <UserStatus status={friend.memberStatus} />
                    </div>
                  </div>
                  <div className="friend-list-info">
                    <p className="friend-name">{friend.nickname}</p>
                  </div>
                </div>
                <div className="friend-list-icons">
                  {selectedMenu === "받은 요청" && (
                    <div
                      className="icons-border icons-accept"
                      onClick={(event) =>
                        handleAcceptFriend(friend.memberId, event)
                      }
                    >
                      <IconAccept />
                    </div>
                  )}
                  <div
                    className="icons-border icons-block"
                    onClick={(event) =>
                      handleDeleteFriend(friend.memberId, event)
                    }
                  >
                    <IconBlock />
                  </div>
                  {selectedMenu === "받은 요청" && (
                    <div
                      className="icons-border icons-cancel"
                      onClick={(event) =>
                        handleBlockFriend(friend.memberId, event)
                      }
                    >
                      <IconCancel />
                    </div>
                  )}
                </div>
              </li>
              <IconLine className="icons-line" />
            </div>
          ))
        ) : (
          <p className="no-friends">{selectedMenu} 이(가) 없습니다.</p>
        )}
      </ul>
    </div>
  );
}

export default Friend;
