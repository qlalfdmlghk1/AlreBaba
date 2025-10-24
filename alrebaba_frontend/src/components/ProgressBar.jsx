import { useEffect, useState } from "react";
import "./ProgressBar.css";
import { getCTInfo } from "../service/codingTest";
import { useNavigate, useParams } from "react-router-dom";

function getTimeDifference(endTime) {
  return Math.floor((new Date(endTime) - new Date()) / 1000); // 현재 시각과 endTime 차이 (초)
}

function getMaxTime(startTime, endTime) {
  return Math.floor((new Date(endTime) - new Date(startTime)) / 1000);
}

function ProgressBar() {
  const [remainingTime, setRemainingTime] = useState(0);
  const [maxTime, setMaxTime] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();
  const { studyId, channelId } = useParams();

  // 코딩 테스트 정보 조회 API 호출
  useEffect(() => {
    async function fetchCTInfoData() {
      try {
        setIsLoading(true);

        const inputTestTime = sessionStorage.getItem("inputTestTime");
        const testStartTime = sessionStorage.getItem("testStartTime");

        if (inputTestTime) {
          const totalSeconds = parseInt(inputTestTime);

          // 테스트 시작 시간이 있으면 경과 시간 계산
          if (testStartTime) {
            const elapsedSeconds = Math.floor(
              (new Date().getTime() - parseInt(testStartTime)) / 1000
            );
            const remaining = Math.max(0, totalSeconds - elapsedSeconds);
            setRemainingTime(remaining);
            setMaxTime(totalSeconds);
          } else {
            // 시작 시간이 없으면 처음 시작하는 것으로 간주
            setRemainingTime(totalSeconds);
            setMaxTime(totalSeconds);
            // 현재 시간을 시작 시간으로 저장
            sessionStorage.setItem("testStartTime", new Date().getTime());
          }
        } else {
          // 입력 시간이 없으면 API에서 시간 정보 가져오기
          const codingTestId = sessionStorage.getItem(`channelId-${channelId}`);
          const response = await getCTInfo({ codingTestId });
          const timeDiff = getTimeDifference(response.data[0].endTime);
          const max = getMaxTime(
            response.data[0].startTime,
            response.data[0].endTime
          );
          setRemainingTime(timeDiff);
          setMaxTime(max);
        }
      } catch (error) {
        console.error("시간 정보를 가져오는데 실패했습니다:", error);
      } finally {
        setIsLoading(false);
      }
    }

    fetchCTInfoData();
  }, [channelId]);

  // 타이머 감소 로직
  useEffect(() => {
    if (isLoading || remainingTime < 0) return;

    const interval = setInterval(() => {
      setRemainingTime((prevTime) => prevTime - 1);
    }, 1000);

    return () => clearInterval(interval);
  }, [remainingTime, isLoading]);

  useEffect(() => {
    // 타이머 시간 종료 시 화면 강제 이동
    if (remainingTime < 0) {
      alert("코딩테스트가 종료됩니다...");
      sessionStorage.removeItem(`channelId-${channelId}`);
      sessionStorage.removeItem("inputTestTime");
      sessionStorage.removeItem("testStartTime");
      sessionStorage.setItem(`channelId-${channelId}-boolean`, false);
      navigate(`/study/${studyId}/${channelId}/`);
    }
  }, [remainingTime, navigate, studyId, channelId]);

  function formatTime(seconds) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    let timeString = "";
    if (hours > 0) {
      timeString += `${hours}시간 `;
    }
    if (minutes > 0 || hours > 0) {
      timeString += `${minutes}분 `;
    }
    if (secs > 0 || minutes > 0 || hours > 0) {
      timeString += `${secs}초`;
    }

    return timeString || "시간이 종료되었습니다.";
  }

  if (isLoading) {
    return <div className="progress-bar">타이머를 로딩 중입니다...</div>;
  }

  return (
    <div className="progress-bar">
      <progress value={remainingTime} max={maxTime}></progress>
      <p>
        {remainingTime > 0
          ? `${formatTime(remainingTime)} 남았습니다...`
          : "시간이 종료되었습니다."}
      </p>
    </div>
  );
}

export default ProgressBar;
