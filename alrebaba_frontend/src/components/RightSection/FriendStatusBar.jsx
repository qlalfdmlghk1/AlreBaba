import "./FriendStatusBar.css";
import IconSearch from "../Icons/IconSearch";
import profile from "/src/assets/images/profile.png";
import UserStatus from "../UserStatus";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getStudyParticipants } from "../../service/studyParticipant";

function convertStatusToKor(status) {
  if (status === "ONLINE") return "온라인";
  if (status === "OFF_LINE") return "오프라인";
  if (status === "NO_INTERFERENCE") return "방해 금지";
  if (status === "ON_ANOTHER_BUSINESS") return "자리 비움";
  return "알 수 없음";
}

function FriendStatusBar() {
  const { studyId } = useParams();
  const [participants, setParticipants] = useState([]); // 스터디 참가자 목록 상태

  useEffect(() => {
    const fetchParticipants = async () => {
      try {
        const data = await getStudyParticipants(studyId);
        // console.log("API 응답 데이터:", data);
        setParticipants(data);
      } catch (error) {
        console.error("스터디 참가자 목록을 가져오는 중 오류 발생:", error);
      }
    };
    fetchParticipants();

    const intervalId = setInterval(fetchParticipants, 10000);

    return () => clearInterval(intervalId);
  }, [studyId]);

  return (
    <section className="friend-status-bar-container">
      {/* 스터디 참가자 목록 */}
      <div className="friend-status-header">
        <p>스터디 참가자 - {participants.length}명</p>
      </div>
      <div className="friend-status-list">
        {participants.length > 0 ? (
          <ul>
            {participants.map((member) => (
              <li key={member.memberId} className="friend-list-item">
                <span className="friend-profile">
                  <img
                    src={member.profileImage || profile}
                    alt={member.nickname}
                  />
                  <span className="user-status">
                    <UserStatus status={member.status} />
                  </span>
                </span>
                <span className="friend-info">
                  <h4>{member.nickname}</h4>
                  <p>{convertStatusToKor(member.status)}</p>
                </span>
              </li>
            ))}
          </ul>
        ) : (
          <p>스터디 참가자가 없습니다.</p>
        )}
      </div>
    </section>
  );
}

export default FriendStatusBar;
