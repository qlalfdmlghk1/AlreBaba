import React, { useEffect, useState } from "react";
import TitleBarContainer from "./TitleBarContainer";
import IconFriend from "./Icons/IconFriend";
import IconProfile from "./Icons/IconProfile";
import IconEvent from "./Icons/IconEvent";
import IconAlert from "./Icons/IconAlert";
import IconHome from "./Icons/IconHome";
import IconHash from "./Icons/IconHash";
import IconMeeting from "./Icons/IconMeeting";
import IconCode from "./Icons/IconCode";
import IconCodingTest from "./Icons/IconCodingTest";
import ModalAddFriend from "./Modals/ModalAddFriend";
import IconAddFriend from "./Icons/IconAddFriend";
import {
  getBlockedFriends,
  getFriendsList,
  getReceivedFriendRequests,
  getSentFriendRequests,
} from "../service/friend";
import "./TitleBar.css";

function TitleBar({
  identifier,
  setFriends,
  channelName,
  selectedMenu,
  setSelectedMenu,
}) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  // 사용자 ID
  const [userId, setUserId] = useState(
    sessionStorage.getItem("userData.memberId")
  );

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);
  let titleIcon;
  let list;

  // 선택한 메뉴에 따라 친구 요청 목록 불러오기
  useEffect(() => {
    if (!userId) return;
    if (identifier !== "친구") return;

    async function fetchFriendRequests() {
      try {
        if (selectedMenu === "모두") {
          const allFriends = await getFriendsList(userId); // ✅ 전체 친구 목록 조회
          setFriends(allFriends);
        } else if (selectedMenu === "보낸 요청") {
          const sentRequests = await getSentFriendRequests(userId);
          setFriends(sentRequests);
        } else if (selectedMenu === "받은 요청") {
          const receivedRequests = await getReceivedFriendRequests(20);
          setFriends(receivedRequests);
        } else if (selectedMenu === "차단 목록") {
          const bannedFriends = await getBlockedFriends();
          setFriends(bannedFriends);
        } else {
          setFriends([]);
        }
      } catch (error) {
        console.error("친구 요청 목록을 불러오는 데 실패했습니다.", error);
        setFriends([]);
      }
    }

    fetchFriendRequests();
  }, [selectedMenu, userId, setFriends, isModalOpen]);

  // 메뉴 클릭 이벤트 핸들러
  const handleMenuClick = async (menu) => {
    setSelectedMenu(menu);
  };

  if (identifier === "친구") {
    titleIcon = <IconFriend color="#000" />;
    list = (
      <div className="title-friend">
        <span className="title-friend-menu">
          {["모두", "보낸 요청", "받은 요청", "차단 목록"].map((menu) => (
            <p
              key={menu}
              className={`menu-item ${selectedMenu === menu ? "active" : ""}`}
              onClick={() => handleMenuClick(menu)}
            >
              {menu}
            </p>
          ))}
        </span>
      </div>
    );
  } else if (identifier === "프로필") {
    titleIcon = <IconProfile color="#000" />;
  } else if (identifier === "Home") {
    titleIcon = <IconHome color="#000" />;
  } else if (identifier === "Event") {
    titleIcon = <IconEvent color="#000" />;
  } else if (identifier === "알림") {
    titleIcon = <IconAlert color="#000" />;
  } else if (identifier === "채팅") {
    titleIcon = <IconHash color="#000" />;
    list = <p>{channelName}</p>;
  } else if (identifier === "음성") {
    titleIcon = <IconMeeting color="#000" />;
    list = <p>{channelName}</p>;
  } else if (identifier === "코드 리뷰") {
    titleIcon = <IconCode color="#000" />;
    list = <p>{channelName}</p>;
  } else if (identifier === "코딩 테스트") {
    titleIcon = <IconCodingTest color="#000" />;
    list = <p>{channelName}</p>;
  }

  return (
    <div className="title-bar">
      <TitleBarContainer titleIcon={titleIcon} name={identifier}>
        {list && "|"}
        {list}
      </TitleBarContainer>
      {identifier === "친구" && (
        <button className="add-friend" onClick={openModal}>
          <IconAddFriend />
        </button>
      )}
      {isModalOpen && <ModalAddFriend onClose={closeModal} />}
    </div>
  );
}

export default TitleBar;
