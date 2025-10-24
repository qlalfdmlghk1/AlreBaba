import React, { useState } from "react";
import { Link } from "react-router-dom";
import profile from "../../assets/images/profile.png";
import IconFriend from "../Icons/IconFriend";
import IconProfile from "../Icons/IconProfile";
import IconAlert from "../Icons/IconAlert";
import IconArrowDown from "../Icons/IconArrowDown";
import IconSearch from "../Icons/IconSearch";
import IconArrowRight from "../Icons/IconArrowRight";
import UserStatus from "../UserStatus";
import ProfilePage from "../../pages/ProfilePage";
import "./StudyNav.css";

function StudyNav() {
  const [isOpened, setIsOpened] = useState(true);

  const handleClick = () => {
    setIsOpened((prevState) => !prevState);
  };

  return (
    <div className="study-nav-container">
      <div className="dialog" />
      <div className="study-name-container">
        <p>마이페이지</p>
      </div>
      <div className="menus">
        <Link to="/profile" className="menu-text">
          <div className="menu">
            <IconProfile />
            프로필
          </div>
        </Link>
        <Link to="/friend" className="menu-text">
          <div className="menu">
            <IconFriend />
            친구
          </div>
        </Link>
        <Link to="/alert" className="menu-text">
          <div className="menu">
            <IconAlert />
            <p>알림</p>
          </div>
        </Link>
      </div>
    </div>
  );
}

export default StudyNav;
