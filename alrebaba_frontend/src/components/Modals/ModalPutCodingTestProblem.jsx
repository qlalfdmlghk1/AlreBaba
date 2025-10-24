import { useEffect, useRef, useState } from "react";
import "./ModalPutCodingTestProblem.css";

function isValidHttpUrl(string) {
  try {
    const newUrl = new URL(string);
    return newUrl.protocol === "http:" || newUrl.protocol === "https:";
  } catch (err) {
    return false;
  }
}

function ModalPutCodingTestProblem({ open, onClose, onNext }) {
  const modal = useRef();
  const [errorMsg, setErrorMsg] = useState("");

  const [problemState, setProblemState] = useState([
    {
      id: 1,
      info: {
        title: "",
        url: "",
      },
    },
  ]);

  function handleInput(identifier, value, id) {
    setProblemState((prevProblem) =>
      prevProblem.map((problem) =>
        problem.id === id
          ? { ...problem, info: { ...problem.info, [identifier]: value } }
          : problem
      )
    );
  }

  const areTwoInputsFilled =
    problemState[problemState.length - 1].info.title &&
    problemState[problemState.length - 1].info.url;

  const greaterThanThree = problemState.length >= 3;

  const isTitleLowerThanHundred =
    Array.from(problemState[problemState.length - 1].info.title).length <= 100;

  useEffect(() => {
    const lastProblem = problemState[problemState.length - 1];

    if (!isValidHttpUrl(lastProblem.info.url) && lastProblem.info.url !== "") {
      setErrorMsg("올바른 URL 형식으로 입력하세요.");
    } else if (greaterThanThree) {
      setErrorMsg("코딩테스트 문제는 최대 3개까지 설정 가능합니다.");
    } else if (!isTitleLowerThanHundred) {
      setErrorMsg("코딩테스트 제목은 최대 100자까지 입력 가능합니다.");
    } else {
      setErrorMsg("");
    }
  }, [problemState]);

  const isNextButtonActive =
    problemState.every((problem) => problem.info.title && problem.info.url) &&
    isValidHttpUrl(problemState[problemState.length - 1].info.url);

  const isLastUrlValid = isValidHttpUrl(
    problemState[problemState.length - 1].info.url
  );

  const canAddProblem =
    areTwoInputsFilled && !greaterThanThree && isLastUrlValid;

  function handleAddProblem() {
    if (errorMsg) return;

    setProblemState((prevProblem) => [
      ...prevProblem,
      {
        id: prevProblem.length + 1,
        info: { title: "", url: "" },
      },
    ]);
  }

  function handleDeleteProblem() {
    if (problemState.length > 1) {
      setProblemState((prev) => prev.slice(0, -1));
    }
  }

  useEffect(() => {
    if (open) {
      modal.current?.showModal();
    } else {
      modal.current?.close();
    }
  }, [open]);

  function handleCancel() {
    modal.current?.close();
    onClose();
  }

  // esc 키를 눌렀을 때 모달이 닫히도록 설정
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
    <dialog className="modal-create-ct" ref={modal} onClose={onClose}>
      <h2 className="coding-test">코딩테스트 설정하기</h2>

      {errorMsg && <p className="error-message">{errorMsg}</p>}

      <div className="coding-test-problem-container">
        {problemState.map((problem) => (
          <div key={problem.id} className="coding-test-problem-input">
            <input
              type="text"
              placeholder="문제 제목을 입력하세요"
              value={problem.info.title}
              onChange={(event) =>
                handleInput("title", event.target.value, problem.id)
              }
            />
            <input
              type="text"
              placeholder="URL을 입력하세요"
              value={problem.info.url}
              onChange={(event) =>
                handleInput("url", event.target.value, problem.id)
              }
            />
          </div>
        ))}

        <div className="coding-test-add-delete">
          <button
            className={`delete ${problemState.length > 1 ? "active" : ""}`}
            onClick={handleDeleteProblem}>
            삭제하기
          </button>
          <button
            className={`add ${canAddProblem ? "active" : ""}`}
            onClick={handleAddProblem}
            disabled={!areTwoInputsFilled || greaterThanThree}>
            추가하기
          </button>
        </div>
      </div>

      <div className="coding-test-create-actions">
        <button className="cancel" onClick={handleCancel}>
          취소하기
        </button>
        <button
          className={`next ${isNextButtonActive ? "active" : ""}`}
          disabled={!isNextButtonActive}
          onClick={() => onNext("create", problemState)}>
          다음으로
        </button>
      </div>
    </dialog>
  );
}

export default ModalPutCodingTestProblem;
