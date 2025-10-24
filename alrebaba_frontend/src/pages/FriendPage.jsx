import { useState, useEffect, useRef } from "react";
import Friend from "../components/Friend";
import TitleBar from "../components/TitleBar";
import InnerHeaderBase from "../components/InnerHeader/InnerHeaderBase";
import InnerFooter from "../components/InnerFooter";
import ChannelStudyBar from "../components/LeftSection/ChannelStudyBar";
import UserInfo from "../components/RightSection/UserInfo";
import StudyUserBar from "../components/LeftSection/StudyUserBar";
import "./FriendPage.css";
import { useParams } from "react-router-dom";

function FriendPage() {
  const [friends, setFriends] = useState([]); // API 데이터 저장
  const [selectedMenu, setSelectedMenu] = useState("모두"); // 현재 선택된 메뉴
  const [selectedFriend, setSelectedFriend] = useState(null);
  const [isUserInfoVisible, setIsUserInfoVisible] = useState(false);

  useEffect(() => {
    if (selectedFriend) {
      setIsUserInfoVisible(true);
    }
  }, [selectedFriend]);

  useEffect(() => {
    if (!isUserInfoVisible) {
      const timer = setTimeout(() => {
        setSelectedFriend(null);
      }, 300);

      return () => clearTimeout(timer);
    }
  }, [isUserInfoVisible]);

  return (
    <>
      <div className="layout-container">
        <div className="left">
          <ChannelStudyBar />
          <StudyUserBar />
        </div>
        <div className="center">
          <TitleBar
            identifier={"친구"}
            setFriends={setFriends}
            channelName={"친구"}
            selectedMenu={selectedMenu}
            setSelectedMenu={setSelectedMenu}
          />
          <InnerHeaderBase />
          <Friend
            friends={friends.content}
            setFriends={setFriends}
            selectedMenu={selectedMenu}
            selectedfriend={selectedFriend}
            setSelectedFriend={setSelectedFriend}
            setIsUserInfoVisible={setIsUserInfoVisible}
          />
          <InnerFooter />
        </div>
        <div
          className={`right-user-info ${isUserInfoVisible ? "visible" : ""}`}
        >
          {selectedFriend && <UserInfo selectedFriend={selectedFriend} />}
        </div>
      </div>
    </>
  );
}

export default FriendPage;
