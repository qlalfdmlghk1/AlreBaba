import { useState } from "react";
import TitleBar from "../components/TitleBar";
import InnerHeaderBase from "../components/InnerHeader/InnerHeaderBase";
import InnerFooter from "../components/InnerFooter";
import ChannelStudyBar from "../components/LeftSection/ChannelStudyBar";
import UserInfo from "../components/RightSection/UserInfo";
import StudyUserBar from "../components/LeftSection/StudyUserBar";
import Profile from "../components/Profile";
import defaultProfileImage from "../assets/images/basicImage.jpg";
import "./FriendPage.css";

function ProfilePage() {
  const storedNickname = sessionStorage.getItem("userData.nickname");
  const storedProfileImg = sessionStorage.getItem("userData.profileImage");
  const [selectedFriend, setSelectedFriend] = useState(null);
  const [nickname, setNickname] = useState(storedNickname);
  const [profileImage, setProfileImage] = useState(
    storedProfileImg || defaultProfileImage
  ); // 프로필 이미지 상태
  return (
    <>
      <div className="layout-container">
        <div className="left">
          <ChannelStudyBar />
          <StudyUserBar
            profileImage={profileImage}
            setProfileImage={setProfileImage}
            nickname={nickname}
            setNickname={setNickname}
          />
        </div>
        <div className="center">
          <TitleBar identifier={"프로필"} />
          <InnerHeaderBase />
          <Profile
            profileImage={profileImage}
            setProfileImage={setProfileImage}
            nickname={nickname}
            setNickname={setNickname}
          />
          <InnerFooter />
        </div>
        <div className={`right-user-info ${selectedFriend ? "visible" : ""}`}>
          {selectedFriend && <UserInfo selectedFriend={selectedFriend} />}
        </div>
      </div>
    </>
  );
}

export default ProfilePage;
