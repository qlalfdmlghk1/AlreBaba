import { useEffect, useRef, useState } from "react";
import "./ModalSetTimer.css";

function ModalSetTimer({ open, onClose, onNext }) {
  const [isTouched, setIsTouched] = useState(false);
  const [inputState, setInputState] = useState({
    hour: "",
    minute: "",
    second: "",
  });

  let errorMsg = "";

  const modal = useRef();

  useEffect(() => {
    if (open) {
      modal.current?.showModal();
    } else {
      modal.current?.close();
    }
  }, [open]);

  // 유효성 검사
  const isInputValid =
    inputState.minute <= 59 &&
    inputState.second <= 59 &&
    inputState.minute >= 0 &&
    inputState.second >= 0;

  const AreInputsFilled =
    inputState.hour !== "" &&
    inputState.minute !== "" &&
    inputState.second !== "";

  const isLowerThanSix =
    Number(inputState.hour) +
      Number(inputState.minute) / 60 +
      Number(inputState.second) / 3600 <=
    6;

  const AreAllZero =
    inputState.hour === 0 &&
    inputState.minute === 0 &&
    inputState.second === 0 &&
    (inputState.hour !== "" ||
      inputState.minute !== "" ||
      inputState.second !== "");

  if (isTouched) {
    if (AreAllZero) {
      errorMsg = "올바른 형식으로 입력하세요.";
    } else if (!isInputValid) {
      errorMsg = "시간 형식이 올바르지 않습니다.";
    } else if (!isLowerThanSix) {
      errorMsg = "코딩테스트는 6시간까지 가능합니다.";
    }
  }

  const isButtonActive =
    isInputValid && AreInputsFilled && !AreAllZero && isLowerThanSix;

  function handleInput(identifier, value) {
    setIsTouched(true);

    let numericValue = parseInt(value, 10);

    if (isNaN(numericValue)) {
      numericValue = 0;
    }

    if (numericValue < 0) {
      numericValue = 0;
    } else if (numericValue > 99) {
      numericValue = 99;
    }

    setInputState({ ...inputState, [identifier]: numericValue });
  }

  const handleInputChange = (e, identifier) => {
    let value = e.target.value;

    if (value.length > 2) {
      value = value.slice(0, 2);
    }

    handleInput(identifier, value);
  };

  useEffect(() => {
    function handleEscape(event) {
      if (event.key === "Escape") {
        modal.current?.close();
        onClose();
      }
    }

    window.addEventListener("keydown", handleEscape);
    return () => {
      window.removeEventListener("keydown", handleEscape);
    };
  }, [onClose]);

  return (
    <dialog className="modal-set-timer" ref={modal}>
      <h2>코딩테스트 설정하기</h2>
      <div className="timer-container">
        <span className="timer-input">
          <label htmlFor="hour">시</label>
          <input
            type="number"
            id="hour"
            name="hour"
            placeholder="00"
            min="0"
            max="99"
            value={inputState.hour}
            onChange={(event) => handleInputChange(event, "hour")}
          />
        </span>
        <span className="colon">:</span>
        <span className="timer-input">
          <label htmlFor="minute">분</label>
          <input
            type="number"
            id="minute"
            name="minute"
            placeholder="00"
            min="0"
            max="59"
            value={inputState.minute}
            onChange={(event) => handleInputChange(event, "minute")}
          />
        </span>
        <span className="colon">:</span>
        <span className="timer-input">
          <label htmlFor="second">초</label>
          <input
            type="number"
            id="second"
            name="second"
            placeholder="00"
            min="0"
            max="59"
            value={inputState.second}
            onChange={(event) => handleInputChange(event, "second")}
          />
        </span>
      </div>
      {errorMsg.length > 0 && <p className="timer-error-msg">{errorMsg}</p>}
      <div className="modal-timer-btn-actions">
        <button onClick={onClose} className="cancel">
          취소하기
        </button>
        <button
          disabled={!isButtonActive}
          className={`next-btn ${isButtonActive ? "active" : ""}`}
          onClick={() => onNext("codingTest", inputState)}>
          다음으로
        </button>
      </div>
    </dialog>
  );
}

export default ModalSetTimer;
