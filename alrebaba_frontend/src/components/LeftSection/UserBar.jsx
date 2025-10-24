import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import IconFullAlert from "../Icons/IconFullAlert";
import StatusMenu from "../StatusMenu";
import UserStatus from "../UserStatus";
import IconAlertOff from "../Icons/IconAlertOff";
import IconAlertActive from "../Icons/IconAlertActive";
import IconLogout from "../Icons/IconLogout";
import { useNavigate } from "react-router-dom";
import { logout, updateMemberStatus } from "../../service/member";
import "./UserBar.css";

function UserBar({
  profileImage,
  setProfileImage,
  nickname,
  setNickname,
  meeting,
}) {
  const navigate = useNavigate();

  const [profileClicked, setProfileClicked] = useState(false);
  const [userStatus, setUserStatus] = useState(
    sessionStorage.getItem("userData.status") || "ONLINE"
  );
  const [isAlertOn, setIsAlertOn] = useState(
    sessionStorage.getItem("userData.isAlarmOn")
  );
  const [userName, setUserName] = useState(
    sessionStorage.getItem("userData.nickname")
  );
  const [profile, setProfile] = useState(
    sessionStorage.getItem("userData.profileImage")
  );
  const [uniqueId, setUniqueId] = useState(
    sessionStorage.getItem("userData.uniqueId")
  );
  const [memberId, setMemberId] = useState(
    sessionStorage.getItem("userData.memberId")
  );
  const [hasNewNotifications, setHasNewNotifications] = useState(
    sessionStorage.getItem("userData.hasNewNotifications") === "true"
  );

  // sessionStorage 변경을 감지하고 상태를 업데이트
  useEffect(() => {
    const handleStorageChange = () => {
      setUserStatus(sessionStorage.getItem("userData.status") || "ONLINE");
      setIsAlertOn(sessionStorage.getItem("userData.isAlarmOn") === "true");
      setUserName(sessionStorage.getItem("userData.nickname") || "사용자");
      setProfile(sessionStorage.getItem("userData.profileImage"));
      setUniqueId(sessionStorage.getItem("userData.uniqueId") || "");
    };

    // storage 이벤트 리스너 추가
    window.addEventListener("storage", handleStorageChange);

    return () => {
      // 컴포넌트 언마운트 시 리스너 제거
      window.removeEventListener("storage", handleStorageChange);
    };
  }, []);

  function handleAlertClick() {
    if (meeting) {
      toast.warn("먼저 음성통화를 종료해주세요.");
      return;
    }
    setIsAlertOn((prevAlertMode) => !prevAlertMode);
    sessionStorage.setItem("userData.isAlarmOn", !isAlertOn);
    // 알림 페이지 진입 시 새 알림 상태 해제
    sessionStorage.setItem("userData.hasNewNotifications", "false");
    navigate("/alert");
  }

  async function handleLogoutClick() {
    if (meeting) {
      toast.warn("먼저 음성통화를 종료해주세요.");
      return;
    }
    const ans = confirm("로그아웃하시겠습니까?");
    if (ans) {
      const memberId = sessionStorage.getItem("userData.memberId");
      const response = await logout(memberId);

      if (response.status === 200) {
        navigate("/");
      } else {
        alert("로그아웃에 실패했습니다.");
      }
    }
  }

  useEffect(() => {
    const handleNotificationUpdate = () => {
      setHasNewNotifications(
        sessionStorage.getItem("userData.hasNewNotifications") === "true"
      );
    };
    window.addEventListener("notificationsUpdated", handleNotificationUpdate);
    return () => {
      window.removeEventListener(
        "notificationsUpdated",
        handleNotificationUpdate
      );
    };
  }, []);

  function handleClick() {
    setProfileClicked((profileClicked) => !profileClicked);
  }

  const selectStatus = async (status) => {
    try {
      const response = await updateMemberStatus(status);
      if (response.success) {
        setUserStatus(status);
        sessionStorage.setItem("userData.status", status);
        setProfileClicked(false);
      } else {
        console.error("❌ 상태 변경 실패:", response.message);
      }
    } catch (error) {
      console.error("❌ 상태 변경 중 오류 발생:", error);
    }
  };

  let koreanStatus;
  switch (userStatus) {
    case "ONLINE":
      koreanStatus = "온라인";
      break;
    case "OFF_LINE":
      koreanStatus = "오프라인";
      break;
    case "ON_ANOTHER_BUSINESS":
      koreanStatus = "자리 비움";
      break;
    default:
      koreanStatus = "방해 금지";
  }

  return (
    <div className="user-bar">
      {profileClicked && <StatusMenu onSelect={selectStatus} />}
      <div className="user-bar-container">
        <div className="user-img">
          <div className="user-img-container">
            <img
              src={profileImage || profile}
              alt="user image"
              onClick={handleClick}
            />
            <div className="status-icon">
              <UserStatus status={userStatus} />
            </div>
          </div>
        </div>
        <div className="user-info">
          <h4 className="user-name">{nickname || userName}</h4>
          <p className="user-unique-id">
            {nickname || userName}@{memberId}
          </p>
        </div>
        <div className="icons">
          <span onClick={handleAlertClick} style={{ cursor: "pointer" }}>
            {/* 새 알림이 있을 경우 IconAlertActive, 없으면 기존 IconFullAlert 렌더링 */}
            {hasNewNotifications ? <IconAlertActive /> : <IconFullAlert />}
          </span>
          <span onClick={handleLogoutClick}>
            <IconLogout />
          </span>
        </div>
      </div>
    </div>
  );
}

export default UserBar;
