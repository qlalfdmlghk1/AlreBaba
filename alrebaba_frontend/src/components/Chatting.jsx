import "./Chatting.css";
import React, { useEffect, useState, useRef, useCallback } from "react";
import { useParams } from "react-router-dom";
import UserStatus from "./UserStatus";
import { myInfo } from "/src/service/member";
import IconSend from "./Icons/IconSend";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { getStudyParticipants } from "/src/service/studyParticipant";

// 시간을 "오전 6:36" 형식(12시간제)으로 표시
function formatTime(timestamp) {
  if (!timestamp) return "";
  const date = new Date(timestamp);
  let hours = date.getHours();
  const minutes = date.getMinutes();
  const period = hours < 12 ? "오전" : "오후";
  if (hours === 0) {
    hours = 12;
  } else if (hours > 12) {
    hours -= 12;
  }
  const formattedMinutes = minutes.toString().padStart(2, "0");
  return `${period} ${hours}:${formattedMinutes}`;
}

// 날짜(YYYY-MM-DD) 변환 함수
function formatDate(timestamp) {
  const date = new Date(timestamp);
  return date.toISOString().split("T")[0];
}

// ChatMessage 컴포넌트를 React.memo로 감싸 최적화
const ChatMessage = React.memo(function ChatMessage({
  chat,
  user,
  participantsMap,
  onDelete,
}) {
  const [showMenu, setShowMenu] = useState(false);

  const handleMenuToggle = useCallback(() => {
    setShowMenu((prev) => !prev);
  }, []);

  const handleDelete = useCallback(() => {
    setShowMenu(false);
    console.log("Trying to delete messageId:", chat.messageId);
    onDelete(chat.messageId);
  }, [chat.messageId, onDelete]);

  const isOwner = user && chat.senderId === user.memberId;
  // chat.senderId를 문자열로 변환하여 lookup
  const participant = participantsMap[String(chat.senderId)];
  const participantName = participant && participant.nickname;
  const participantImage = participant && participant.profileImage;
  const participantStatus = participant && participant.status;
  const loggedInUserImage = sessionStorage.getItem("userData.profileImage");
  const profileImageSrc = isOwner ? loggedInUserImage : participantImage;

  console.log(
    "ChatMessage - senderId:",
    chat.senderId,
    "participant:",
    participant
  );

  return (
    <div className="chat-message">
      <span className="chat-user-info">
        <img
          src={profileImageSrc}
          alt="Profile"
          className="chat-profile"
          onError={(e) => {
            e.target.onerror = null;
            e.target.src = profileImageSrc;
          }}
        />
        <span className="chat-user-status">
          <UserStatus status={participantStatus} />
        </span>
      </span>
      <span className="chat-message-info">
        <div className="chat-header">
          <span className="chat-name">{participantName}</span>
          <span className="chat-time">{formatTime(chat.createdAt)}</span>
          {isOwner && (
            <div className="chat-menu-container">
              <button className="chat-menu-trigger" onClick={handleMenuToggle}>
                ···
              </button>
              {showMenu && (
                <div className="chat-menu">
                  <div className="chat-menu-item" onClick={handleDelete}>
                    메시지 삭제
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
        <p className="chat-text">{chat.content}</p>
      </span>
    </div>
  );
});

function Chatting({ channelName }) {
  const { studyId, channelId } = useParams();
  // useParams로 받은 값은 문자열이므로 숫자로 변환
  const parsedStudyId = Number(studyId);
  const parsedChannelId = Number(channelId);

  const [message, setMessage] = useState("");
  const [groupedChatMessages, setGroupedChatMessages] = useState({});
  const [user, setUser] = useState(null);
  const [participantsMap, setParticipantsMap] = useState({});
  const [isConnected, setIsConnected] = useState(false);

  const stompClientRef = useRef(null);
  const chattingContainerRef = useRef(null);

  const API_BASE_URL = "https://i12a702.p.ssafy.io/api/v1";
  const BASE_WS_URL = "https://i12a702.p.ssafy.io/api/v1/ws-stomp";

  // 메시지 업데이트 시 스크롤 최하단으로 이동
  useEffect(() => {
    if (chattingContainerRef.current) {
      chattingContainerRef.current.scrollTop =
        chattingContainerRef.current.scrollHeight;
    }
  }, [groupedChatMessages]);

  // 초기 메시지 로딩
  useEffect(() => {
    async function fetchChatMessages() {
      try {
        const response = await fetch(
          `${API_BASE_URL}/studies/${parsedStudyId}/channels/${parsedChannelId}/chats`,
          { headers: { access: sessionStorage.getItem("accessToken") } }
        );
        if (response.ok) {
          const data = await response.json();
          console.log("Fetched chat data:", data);
          setGroupedChatMessages(data);
        } else {
          console.error("Failed to fetch chat messages:", response.status);
        }
      } catch (error) {
        console.error("Error fetching chat messages:", error);
      }
    }
    if (parsedStudyId && parsedChannelId) {
      fetchChatMessages();
    }
  }, [parsedStudyId, parsedChannelId, API_BASE_URL]);

  // 스터디 참가자 목록 불러와 participantsMap 구성
  useEffect(() => {
    async function fetchParticipantsMap() {
      try {
        const mergedMembers = await getStudyParticipants(parsedStudyId);
        console.log("Fetched merged members:", mergedMembers);
        if (mergedMembers && Array.isArray(mergedMembers)) {
          const map = mergedMembers.reduce((acc, member) => {
            acc[String(member.memberId)] = member;
            return acc;
          }, {});
          setParticipantsMap(map);
        }
      } catch (error) {
        console.error("Error fetching study participants:", error);
      }
    }
    if (parsedStudyId) {
      fetchParticipantsMap();
    }
  }, [parsedStudyId]);

  // WebSocket 연결 및 구독
  useEffect(() => {
    const socket = new SockJS(BASE_WS_URL);
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: { access: sessionStorage.getItem("accessToken") },
      reconnectDelay: 5000,
      onConnect: () => {
        setIsConnected(true);
        client.subscribe(`/topic/chat/${parsedChannelId}`, (msg) => {
          if (msg.body) {
            const receivedMessage = JSON.parse(msg.body);
            const dateKey = formatDate(receivedMessage.createdAt);
            setGroupedChatMessages((prev) => {
              const newState = { ...prev };
              if (newState[dateKey]) {
                newState[dateKey] = [...newState[dateKey], receivedMessage];
              } else {
                newState[dateKey] = [receivedMessage];
              }
              return newState;
            });
          }
        });
        client.subscribe("/topic/chat/delete", (msg) => {
          if (msg.body) {
            const deletedId = msg.body;
            setGroupedChatMessages((prev) => {
              const newState = { ...prev };
              Object.keys(newState).forEach((date) => {
                newState[date] = newState[date].filter(
                  (chat) => chat.messageId !== deletedId
                );
              });
              return newState;
            });
          }
        });
      },
      onStompError: (frame) => {
        console.error("Broker reported error:", frame.headers["message"]);
        console.error("Additional details:", frame.body);
      },
    });
    client.activate();
    stompClientRef.current = client;

    async function fetchUserInfo() {
      const response = await myInfo();
      if (response.success) {
        setUser(response.data);
      } else {
        console.error("Failed to fetch user info");
      }
    }
    fetchUserInfo();

    return () => {
      client.deactivate();
    };
  }, [parsedChannelId, BASE_WS_URL]);

  // 메시지 전송 함수
  const sendChatMessage = useCallback(() => {
    if (stompClientRef.current && isConnected && message.trim() !== "") {
      const chatRequest = {
        channelId: parsedChannelId,
        senderId: user ? user.memberId : null,
        senderName: user ? user.nickname : "Unknown",
        content: message.trim(),
      };
      stompClientRef.current.publish({
        destination: "/app/chat/send",
        body: JSON.stringify(chatRequest),
      });
      setMessage("");
    }
  }, [isConnected, message, parsedChannelId, user]);

  // 메시지 삭제 함수 (useCallback으로 메모이제이션)
  const deleteChatMessage = useCallback(
    async (messageId) => {
      if (!messageId) {
        console.error("No messageId provided for deletion");
        return;
      }
      try {
        const response = await fetch(
          `${API_BASE_URL}/studies/${parsedStudyId}/channels/${parsedChannelId}/chats/${messageId}`,
          {
            method: "DELETE",
            headers: { access: sessionStorage.getItem("accessToken") },
          }
        );
        if (!response.ok) {
          console.error("Failed to delete message. Status:", response.status);
        }
      } catch (error) {
        console.error("Error deleting message:", error);
      }
    },
    [API_BASE_URL, parsedStudyId, parsedChannelId]
  );

  const handleEnter = useCallback(
    (event) => {
      if (event.key === "Enter" && message.trim()) {
        sendChatMessage();
      }
    },
    [message, sendChatMessage]
  );

  const handleClick = useCallback(() => {
    if (message.trim()) {
      sendChatMessage();
    }
  }, [message, sendChatMessage]);

  return (
    <section className="chatting-room">
      <div className="chatting-room-scroll" ref={chattingContainerRef}>
        <div className="chatting-room-info">
          <div className="icon">#</div>
          <h2>Welcome to #{channelName}</h2>
          <p>{channelName} 채널이 시작되었습니다. 자유롭게 이야기해주세요.</p>
        </div>
        <div className="chatting-room-container">
          {Object.keys(groupedChatMessages)
            .filter((date) => groupedChatMessages[date]?.length > 0)
            .sort()
            .map((date) => (
              <div key={date} className="chat-group">
                <div className="chat-date">{date}</div>
                {groupedChatMessages[date].map((chat, index) => (
                  <ChatMessage
                    key={index}
                    chat={chat}
                    user={user}
                    participantsMap={participantsMap}
                    onDelete={deleteChatMessage}
                  />
                ))}
              </div>
            ))}
        </div>
      </div>

      <div className="chatting-input-container">
        <input
          placeholder={`#${channelName}에 메시지 보내기`}
          value={message}
          onChange={(e) => setMessage(e.target.value.slice(0, 250))}
          onKeyDown={handleEnter}
        />
        <span className="char-count">{message.length} / 250</span>
        <span className="icons" onClick={handleClick}>
          <IconSend color={message.trim() === "" ? "#74757C" : "#000"} />
        </span>
      </div>
    </section>
  );
}

export default Chatting;
