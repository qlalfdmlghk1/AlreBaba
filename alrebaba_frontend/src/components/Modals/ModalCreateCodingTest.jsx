import { useState } from "react";
import ModalPutCodingTestProblem from "./ModalPutCodingTestProblem.jsx";
import ModalSetTimer from "./ModalSetTimer.jsx";
import { createCT, getCTProblems } from "../../service/codingTest.js";
import { useNavigate, useParams } from "react-router-dom";

function getFormattedTime(date) {
  const localDate = new Date(date.getTime() + 9 * 60 * 60 * 1000);
  return localDate.toISOString().split(".")[0];
}

function generateTime(hours, minutes, seconds) {
  const now = new Date();
  const startTime = getFormattedTime(now);
  const endTimeDate = new Date(now);
  endTimeDate.setHours(endTimeDate.getHours() + hours);
  endTimeDate.setMinutes(endTimeDate.getMinutes() + minutes);
  endTimeDate.setSeconds(endTimeDate.getSeconds() + seconds);
  const endTime = getFormattedTime(endTimeDate);

  return {
    startTime,
    endTime,
  };
}

let reqBody = {};

function ModalCreateCodingTest({ open, onClose }) {
  const [step, setStep] = useState("timer");
  const { channelId } = useParams();
  const navigate = useNavigate();

  async function handleNext(step, input) {
    if (step === "codingTest") {
      const totalSeconds =
        parseInt(input.hour) * 3600 +
        parseInt(input.minute) * 60 +
        parseInt(input.second);

      sessionStorage.setItem("inputTestTime", totalSeconds);

      const { startTime, endTime } = generateTime(
        input.hour,
        input.minute,
        input.second
      );

      const data = { channelId: Number(channelId), startTime, endTime };
      reqBody = { ...data };
      setStep("codingTest");
    } else if (step === "create") {
      const reqData = [];
      input.forEach((item) => {
        reqData.push({
          problemTitle: item.info.title,
          problemUrl: item.info.url,
        });
      });

      const data = {
        ...reqBody,
        problemCreateRequestList: reqData,
      };

      const response = await createCT(data);
      if (!response.data.codingTestId) {
        alert("코딩 테스트를 생성할 수 없습니다.");
        return;
      }

      const problemData = await getCTProblems(response.data.codingTestId);

      // storage에 testId 저장
      sessionStorage.setItem(
        `channelId-${channelId}`,
        response.data.codingTestId
      );

      const inputTestTime = sessionStorage.getItem("inputTestTime");
      if (inputTestTime) {
        sessionStorage.setItem("inputTestTime", inputTestTime);

        sessionStorage.setItem("testStartTime", new Date().getTime());
      }

      navigate(
        `${response.data.codingTestId}/${problemData.data[0].problemId}`
      );
    }
  }

  return (
    <>
      {step === "codingTest" && (
        <ModalPutCodingTestProblem
          open={open}
          onClose={onClose}
          onNext={handleNext}
        />
      )}
      {step === "timer" && (
        <ModalSetTimer open={open} onClose={onClose} onNext={handleNext} />
      )}
    </>
  );
}

export default ModalCreateCodingTest;
