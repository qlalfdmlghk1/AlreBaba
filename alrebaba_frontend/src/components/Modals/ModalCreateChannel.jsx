import { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";
import "./ModalCreateChannel.css";
import IconHash from "../Icons/IconHash";
import IconMeeting from "../Icons/IconMeeting";
import IconCode from "../Icons/IconCode";
import IconCodingTest from "../Icons/IconCodingTest";
import { createChannel, updateChannelName } from "../../service/channel";
import { useParams } from "react-router-dom";

const CHANNEL_INFO = [
  {
    id: 1,
    icon: <IconHash />,
    name: "채팅",
    channelType: "CHAT",
    description: "메세지, 의견, 농담으로 소통하세요",
  },
  {
    id: 2,
    icon: <IconMeeting />,
    name: "음성",
    channelType: "MEETING",
    description: "음성, 화상으로 함께 어울리세요",
  },
  {
    id: 3,
    icon: <IconCode />,
    name: "코드 리뷰",
    channelType: "CODE",
    description: "실시간으로 코드를 공동 편집해보세요",
  },
  {
    id: 4,
    icon: <IconCodingTest />,
    name: "코딩 테스트",
    channelType: "TEST",
    description: "모두 테스트로 실력을 향상시켜보세요",
  },
];

function ModalCreateChannel({
  open,
  onClose,
  setChannelEvent,
  channelId,
  name,
  role,
}) {
  const [selectedChannel, setSelectedChannel] = useState(1);
  const [channelName, setChannelName] = useState("");
  const createChannelModal = useRef();
  const [errorMsg, setErrorMsg] = useState("");
  const { studyId } = useParams(); // studyId 값 가져오기

  // OWNER 역할만 볼 수 있는 채널 정보 필터링
  const filteredChannelInfo = CHANNEL_INFO.filter(
    (channel) => channel.channelType !== "TEST" || role === "OWNER"
  );

  function handleSelect(id) {
    setSelectedChannel(id);
  }

  useEffect(() => {
    function handleKeyDown(event) {
      if (event.key === "Escape") {
        onClose();
      }
    }

    if (open) {
      createChannelModal.current?.showModal();
      window.addEventListener("keydown", handleKeyDown); // 이벤트 리스너 추가
    } else {
      createChannelModal.current?.close();
    }

    return () => {
      window.removeEventListener("keydown", handleKeyDown); // clean up
    };
  }, [open, onClose]);

  useEffect(() => {
    if (name) {
      setChannelName(name);
    }
  }, [name]);

  function handleSubmit(event) {
    if (channelName === "" || channelName === undefined) {
      setErrorMsg("채널 이름은 필수입니다.");
      return;
    } else if (channelName.length < 2 || channelName.length > 15) {
      setErrorMsg("채널 이름은 2~15자 사이로 입력하세요.");
      return;
    }

    setErrorMsg(" ");

    // 선택된 채널 정보 가져오기
    const selectedInfo = filteredChannelInfo.find(
      (channel) => channel.id === selectedChannel
    );

    createChannel(studyId, selectedInfo, channelName)
      .then((response) =>
        response === 201
          ? (toast.success("채널이 성공적으로 생성되었습니다."),
            setChannelEvent(true),
            onClose())
          : null
      )
      .catch(
        (error) => (console.log(error), toast.error("채널 생성을 실패했습니다"))
      );
  }

  function handleUpdate() {
    if (channelName === "" || channelName === undefined) {
      setErrorMsg("채널 이름은 필수입니다.");
      return;
    } else if (channelName.length < 2 || channelName.length > 15) {
      setErrorMsg("채널 이름은 2~15자 사이로 입력하세요.");
      return;
    }

    setErrorMsg("");

    updateChannelName(studyId, channelId, channelName)
      .then((response) => {
        toast.success("채널 이름이 성공적으로 변경되었습니다.");
        setChannelEvent(true);
        onClose();
      })
      .catch((error) => {
        console.error(error);
        toast.error("채널 이름 변경을 실패했습니다.");
      });
  }

  function hanldCancel() {
    createChannelModal.current?.close();
    onClose();
  }

  // 선택된 채널 정보 찾기
  const selectedChannelInfo = filteredChannelInfo.find(
    (channel) => channel.id === selectedChannel
  );

  return (
    <dialog
      className={`create-channel-modal ${name ? "edit" : ""}`}
      ref={createChannelModal}
    >
      {!name ? <h2>채널을 생성해주세요</h2> : <h2>채널 편집</h2>}
      {/* 채널 종류 */}
      {!name && (
        <div className="channel-container">
          {filteredChannelInfo.map((channel) => (
            <div
              key={channel.id}
              className={`channel-item-container ${
                selectedChannel === channel.id ? "active" : ""
              }`}
              onClick={() => handleSelect(channel.id)}
            >
              <div className="channel-icon">{channel.icon}</div>
              <div className="channel-info">
                <p className="channel-name">{channel.name}</p>
                <p className="channel-description">{channel.description}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* 채널 이름 입력 */}
      <div className="create-channel-input">
        <h4>채널 이름</h4>
        <div className="channel-name-container">
          <div className="channel-icon">{selectedChannelInfo.icon}</div>
          <input
            type="text"
            placeholder={`${selectedChannelInfo.name} 채널`}
            value={channelName}
            onChange={(event) => setChannelName(event.target.value)}
          />
        </div>
        <div className="error-message">{errorMsg}</div>
      </div>

      <hr />
      {/* 취소 & 생성 버튼 */}
      <div className="channel-create-actions">
        <button className="create-cancel" onClick={hanldCancel}>
          취소
        </button>
        {!name ? (
          <button className="create-confirm" onClick={handleSubmit}>
            생성
          </button>
        ) : (
          <button className="create-confirm" onClick={handleUpdate}>
            수정
          </button>
        )}
      </div>
    </dialog>
  );
}

export default ModalCreateChannel;
