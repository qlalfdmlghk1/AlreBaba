import { useEffect, useRef } from "react";
import "./ModalCloseCodingTest.css";
import { useNavigate, useParams } from "react-router-dom";

function ModalCloseCodingTest({ open, onClose }) {
  const modal = useRef();
  const navigate = useNavigate();
  const { studyId, channelId, testId } = useParams();

  useEffect(() => {
    if (open) {
      modal.current?.showModal();
    } else {
      modal.current?.close();
    }
  }, [open]);

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

  async function handleSubmit() {
    // testId 제거
    sessionStorage.removeItem(`channelId-${channelId}`);
    sessionStorage.setItem(`channelId-${channelId}-boolean`, false);
    modal.current?.close();
    onClose();

    navigate(`/study/${studyId}/${channelId}`);
  }

  return (
    <dialog className="modal-close-ct" ref={modal}>
      <h2>제출하시겠습니까?</h2>
      <p>제출하신 뒤에는 다시 참여 불가능합니다.</p>
      <div className="modal-close-ct-btn-actions">
        <button onClick={onClose}>돌아가기</button>
        <button onClick={handleSubmit}>제출하기</button>
      </div>
    </dialog>
  );
}

export default ModalCloseCodingTest;
