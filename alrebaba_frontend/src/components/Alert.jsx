import ChannelImg from "../assets/images/channel-img.png";
import IconCancel from "./Icons/IconCancel";
import IconBlock from "./Icons/IconBlock";
import IconAccept from "./Icons/IconAccept";
import IconIgnore from "./Icons/IconIgnore";
import "./Alert.css";
import { useEffect, useState, useCallback } from "react";
import { deleteNotification, getNotifications } from "../service/notification";
import { acceptStudyInvitation, rejectStudyInvitation } from "../service/studyParticipant";
import { acceptOrBlockFriend, deleteFriend } from "../service/friend";

function Alert({ studyAccept }) {
  const [notifications, setNotifications] = useState([]);

  // 알림 데이터를 다시 불러오는 함수를 useCallback으로 정의하여 이벤트 후 호출
  const fetchNotifications = useCallback(async () => {
    try {
      const data = await getNotifications();
      console.log("새로운 알림 데이터:", data);
      setNotifications(data);
    } catch (error) {
      console.error("알림 데이터를 가져오는 중 오류 발생:", error);
    }
  }, []);

  // 컴포넌트 마운트 시 한 번 호출
  useEffect(() => {
    fetchNotifications();
  }, [fetchNotifications]);

  // STUDY_JOIN_REQUEST 알림 수락 처리
  const handleAccept = async (studyId, notificationId) => {
    try {
      console.log(notificationId);
      await acceptStudyInvitation(studyId, notificationId);
      // 이벤트 처리 후 알림 새로고침
      await fetchNotifications();
      if (studyAccept) studyAccept();
    } catch (error) {
      console.error("스터디 초대 수락 오류:", error);
    }
  };

  // STUDY_JOIN_REQUEST 알림 거절 처리
  const handleReject = async (studyId, notificationId) => {
    try {
      console.log(notificationId);
      await rejectStudyInvitation(studyId, notificationId);
      await fetchNotifications();
    } catch (error) {
      console.error("스터디 초대 거절 오류:", error);
    }
  };

  // FRIEND_INVITATION 알림 수락 처리
  const handleAcceptFriendRequest = async (friendId, notificationId) => {
    try {
      alert("🎉 친구 요청을 수락했습니다!");
      console.log(notificationId);
      await acceptOrBlockFriend(friendId, "FOLLOWING");
      await fetchNotifications();
    } catch (error) {
      console.error("친구 요청 수락 오류:", error);
    }
  };

  // FRIEND_INVITATION 알림 차단 처리
  const handleBlockFriendRequest = async (friendId, notificationId) => {
    try {
      alert("🚫 친구를 차단했습니다!");
      await acceptOrBlockFriend(friendId, "BANNED");
      await fetchNotifications();
    } catch (error) {
      console.error("친구 요청 차단 오류:", error);
    }
  };

  // FRIEND_INVITATION 알림 거절 처리
  const handleRejectFriendRequest = async (friendId, notificationId) => {
    try {
      await deleteFriend(friendId);
      await deleteNotification(notificationId);
      await fetchNotifications();
    } catch (error) {
      console.error("친구 요청 거절 오류:", error);
    }
  };

  // EVENT_REMINDER 알림 확인 처리
  const handleEventReminderConfirm = async (notificationId) => {
    try {
      await deleteNotification(notificationId);
      await fetchNotifications();
    } catch (error) {
      console.error("이벤트 리마인더 확인 오류:", error);
    }
  };

  // 메시지 내 senderName을 Bold 처리하는 함수
  const highlightSenderName = (text, senderName) => {
    if (!text || !senderName) return text;
    const regex = new RegExp(senderName, "gi");
    const elements = [];
    let lastIndex = 0;
    let match;

    while ((match = regex.exec(text)) !== null) {
      const start = match.index;
      const end = regex.lastIndex;
      if (start > lastIndex) {
        elements.push(text.slice(lastIndex, start));
      }
      elements.push(
        <span key={start} style={{ fontWeight: "bold" }}>
          {text.slice(start, end)}
        </span>
      );
      lastIndex = end;
    }
    if (lastIndex < text.length) {
      elements.push(text.slice(lastIndex));
    }
    return elements;
  };

  return (
    <div className="alert-container">
      {notifications.length === 0 ? (
        <p className="no-alerts">알람이 없어요</p>
      ) : (
        notifications.map((notification) => (
          <div key={notification.notificationId}>
            <div className="alert-item-container">
              <span className="alert-from-profile">
                <img src={notification.senderImage || ChannelImg} alt="user profile" />
              </span>
              <span className="alert-info-actions-container">
                <span className="alert-info-container">
                  <p className="alert-info-date">
                    {new Date(notification.createdAt).toLocaleString("ko-KR", {
                      year: "numeric",
                      month: "numeric",
                      day: "numeric",
                      hour: "numeric",
                      minute: "numeric",
                      hour12: true,
                    })}
                  </p>
                  <p className="alert-info">{highlightSenderName(notification.message, notification.senderName)}</p>
                </span>
                <span className="alert-actions">
                  {notification.type === "STUDY_JOIN_REQUEST" && (
                    <>
                      <span
                        className="green"
                        onClick={() => handleAccept(notification.senderId, notification.notificationId)}
                      >
                        <IconAccept />
                      </span>
                      <span
                        className="red"
                        onClick={() => handleReject(notification.senderId, notification.notificationId)}
                      >
                        <IconIgnore />
                      </span>
                    </>
                  )}
                  {notification.type === "FRIEND_INVITATION" && (
                    <>
                      <span
                        className="green"
                        onClick={() => handleAcceptFriendRequest(notification.senderId, notification.notificationId)}
                      >
                        <IconAccept />
                      </span>
                      <span
                        className="grey"
                        onClick={() => handleRejectFriendRequest(notification.senderId, notification.notificationId)}
                      >
                        <IconBlock />
                      </span>
                      <span
                        className="red"
                        onClick={() => handleBlockFriendRequest(notification.senderId, notification.notificationId)}
                      >
                        <IconCancel />
                      </span>
                    </>
                  )}
                  {notification.type === "EVENT_REMINDER" && (
                    <>
                      <span className="green" onClick={() => handleEventReminderConfirm(notification.notificationId)}>
                        <IconAccept />
                      </span>
                    </>
                  )}
                </span>
              </span>
            </div>
            <hr />
          </div>
        ))
      )}
    </div>
  );
}

export default Alert;
