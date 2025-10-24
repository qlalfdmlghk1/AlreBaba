import React, { useEffect, useState, useRef } from "react";
import IconLetter from "../Icons/IconLetter";
import { inviteStudyParticipant } from "../../service/studyParticipant";
import { myInfo } from "../../service/member";
import { getFriendsList } from "../../service/friend";
import "./ModalBase.css";
import "./ModalInviteStudyFriend.css";

function ModalInviteStudyFriend({ studyId, onClose }) {
  const [search, setSearch] = useState("");
  const [friends, setFriends] = useState([]); // 친구 목록 상태
  const [myMemberId, setMyMemberId] = useState(null); // 내 회원 ID
  const [error, setError] = useState(""); // 에러 메시지 상태 추가

  const modalRef = useRef(null);

  // 내 회원 ID 가져오기
  useEffect(() => {
    myInfo()
      .then((data) => {
        if (data.success) {
          setMyMemberId(data.data.memberId);
        }
      })
      .catch((error) => {
        console.error("❌ 내 정보 조회 실패:", error);
      });
  }, []);

  // 친구 목록 가져오기
  useEffect(() => {
    if (myMemberId) {
      getFriendsList(myMemberId)
        .then((data) => {
          if (Array.isArray(data.content)) {
            setFriends(data.content);
          } else {
            setFriends([]);
          }
        })
        .catch((error) => {
          console.error("❌ 친구 목록 조회 오류:", error);
          setFriends([]);
        });
    }
  }, [myMemberId]);

  const handleInvite = async (inviteeId, friendName) => {
    try {
      const result = await inviteStudyParticipant(studyId, inviteeId);
      if (result) {
        alert(`${friendName}님을 초대했습니다.`);
      }
    } catch (error) {
      console.error("초대 요청 중 오류 발생:", error);
    }
  };

  useEffect(() => {
    // 외부 클릭 시 모달 닫기
    const handleClickOutside = (event) => {
      if (modalRef.current && !modalRef.current.contains(event.target)) {
        onClose(); // 모달을 닫는 함수 호출
      }
    };
    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [onClose]);

  // 친구 입력 글자수 제한한
  const handleSearchChange = (e) => {
    const value = e.target.value;
    if (value.length > 15) {
      setError("15자까지만 입력 가능합니다.");
      return;
    } else {
      setError("");
      setSearch(value);
    }
  };

  return (
    <div className="container">
      <div className="modal" ref={modalRef}>
        <span>스터디에 친구를 초대하세요</span>
        <input
          type="text"
          placeholder="친구 찾기"
          value={search}
          onChange={handleSearchChange}
          className="search-input"
        />
        {error && <p className="error-message">{error}</p>}
        <div className="friend-invite-list">
          {friends
            .filter((friend) => friend.nickname.toLowerCase().includes(search.toLowerCase()))
            .map((friend) => (
              <div key={friend.memberId} className="friend-item">
                <img src={friend.profileImage} alt={friend.nickname} className="friend-avatar" />
                <span>{friend.nickname}</span>
                <button className="invite-button" onClick={() => handleInvite(friend.memberId, friend.nickname)}>
                  <IconLetter />
                </button>
              </div>
            ))}
        </div>
        <button className="close-button" onClick={onClose}>
          닫기
        </button>
      </div>
    </div>
  );
}

export default ModalInviteStudyFriend;
