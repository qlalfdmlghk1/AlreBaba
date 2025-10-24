import React, { useCallback, useEffect, useState } from "react";
import { searchFriends, sendFriendRequest } from "../../service/friend.js";
import "./ModalBase.css";
import "./ModalAddFriend.css";

function ModalAddFriend({ onClose }) {
  const [search, setSearch] = useState("");
  const [friends, setFriends] = useState([]);
  const [lastId, setLastId] = useState(null);
  const [error, setError] = useState(""); // 에러 메시지 상태 추가
  const [isLoading, setIsLoading] = useState(false); // 검색 중 상태
  const [isSearched, setIsSearched] = useState(false); // 검색 여부
  const pageSize = 10;
  const searchTimeoutRef = React.useRef(null);

  const fetchFriends = useCallback(async () => {
    if (search.trim().length < 1) return;

    setIsLoading(true);
    setIsSearched(false);

    try {
      const results = await searchFriends(search, lastId, pageSize);
      setFriends(Array.isArray(results) ? results : []);
    } catch (error) {
      console.error("❌ 검색 오류:", error);
    } finally {
      setIsLoading(false);
      setIsSearched(true);
    }
  }, [search]);

  useEffect(() => {
    if (search.trim().length < 1) {
      setFriends([]);
      setIsSearched(false);
      setIsLoading(false);
      return;
    }

    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    searchTimeoutRef.current = setTimeout(() => {
      fetchFriends();
    }, 1200);

    return () => clearTimeout(searchTimeoutRef.current);
  }, [search, fetchFriends]);

  const getProfileImage = (profileImage) => {
    return profileImage === "profile/basicImage.jpg"
      ? "src/assets/images/profile.png"
      : profileImage;
  };

  const handleFriendRequest = async (acceptId) => {
    console.log("[친구 요청 버튼 클릭] acceptId:", acceptId);
    const response = await sendFriendRequest(acceptId);
    if (response) {
      console.log("[친구 요청 성공] 응답 데이터:", response);
      alert("🎉친구 요청이 성공적으로 전송되었습니다.");

      // 요청 상태 업데이트: 버튼을 "요청 중"으로 변경
      setFriends((prevFriends) =>
        prevFriends.map((friend) =>
          friend.memberId === acceptId
            ? { ...friend, friendStatus: "REQUESTED" }
            : friend
        )
      );
    } else {
      console.error("[친구 요청 실패] 응답 데이터 없음");
    }
  };

  // 친구 입력 글자수 제한
  const handleSearchChange = (e) => {
    const value = e.target.value;
    if (value.length > 20) {
      setError("20자까지만 입력 가능합니다.");
      return;
    } else {
      setError("");
      setSearch(value);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
      fetchFriends();
    }
  };

  return (
    <div className="container">
      <div className="modal">
        <span>친구를 추가해보세요!!</span>
        <input
          type="text"
          placeholder="친구 찾기"
          value={search}
          onChange={handleSearchChange}
          onKeyDown={handleKeyDown}
          className="search-input"
        />
        {error && <p className="error-message">{error}</p>}
        <div className="add-friend-list">
          {isLoading ? (
            <p className="friend-loading-message">검색 중입니다...</p>
          ) : isSearched && friends.length === 0 ? (
            <p className="no-results">검색 결과가 없습니다.</p>
          ) : (
            friends.map((friend, index) => {
              const friendStatus = friend.friendStatus;

              return (
                <div key={index} className="add-friend-item">
                  <img
                    src={getProfileImage(friend.profileImage)}
                    alt={friend.nickname}
                    className="friend-avatar"
                  />
                  <span>{friend.nickname}</span>

                  <button
                    className={`friend-request ${
                      friendStatus === "FOLLOWING"
                        ? "friend-following"
                        : friendStatus === "REQUESTED"
                        ? "friend-requested"
                        : ""
                    }`}
                    onClick={() =>
                      friendStatus !== "FOLLOWING" &&
                      friendStatus !== "REQUESTED" &&
                      handleFriendRequest(friend.memberId)
                    }
                    disabled={
                      friendStatus === "FOLLOWING" ||
                      friendStatus === "REQUESTED"
                    }
                  >
                    {friendStatus === "FOLLOWING"
                      ? "친구"
                      : friendStatus === "REQUESTED"
                      ? "요청 중"
                      : "친구 신청"}
                  </button>
                </div>
              );
            })
          )}
        </div>
        <button className="close-button" onClick={onClose}>
          닫기
        </button>
      </div>
    </div>
  );
}

export default ModalAddFriend;
