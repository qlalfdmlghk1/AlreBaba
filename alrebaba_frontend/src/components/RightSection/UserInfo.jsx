import React, { useEffect, useState } from "react";
import profile from "../../assets/images/profile.png";
import IconPersonCheck from "../Icons/IconPersonCheck";
import IconPersonBlock from "../Icons/IconPersonBlock";
import "./UserInfo.css";
import { searchFriends } from "../../service/friend";

function UserInfo({ selectedFriend }) {
  const createdDate = selectedFriend.createdAt.split("-");
  const [year, setYear] = useState(createdDate[0]);
  const [month, setMonth] = useState(createdDate[1]);
  const [day, setDay] = useState(createdDate[2]);

  return (
    <div className="user-info-container">
      <div className="user-info-background">
        <div className="user-profile-image-container">
          <img
            className="user-profile-image"
            src={selectedFriend.profileImage}
            alt="user profile"
          />
        </div>
        <div className="accept-block-icon">
          {/* <div className="accept-block-icons">
            <IconPersonCheck />
          </div>
          <div className="accept-block-icons">
            <IconPersonBlock />
          </div> */}
        </div>
      </div>

      <div className="user-info-detail">
        <div className="user-info-contents">
          <h2>{selectedFriend.nickname}</h2>
          <div className="user-registration-container">
            <h5>가입 시기 :</h5>
            <p>
              {year}년 {month}월 {day}일
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default UserInfo;
