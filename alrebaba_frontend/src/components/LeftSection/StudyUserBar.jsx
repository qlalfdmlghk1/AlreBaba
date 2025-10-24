import "./StudyUserBar.css";
import StudyNav from "./StudyNav";
import UserBar from "./UserBar";
import React from "react";

function StudyUserBar({
  profileImage,
  setProfileImage,
  nickname,
  setNickname,
}) {
  return (
    <div className="left-sidebar-container">
      <StudyNav />
      <UserBar
        profileImage={profileImage}
        setProfileImage={setProfileImage}
        nickname={nickname}
        setNickname={setNickname}
      />
    </div>
  );
}

export default StudyUserBar;
