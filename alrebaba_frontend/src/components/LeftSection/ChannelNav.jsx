import { useState, navigate, useEffect, useRef } from "react";
import { toast } from "react-toastify";
import { useNavigate, useParams } from "react-router-dom";
import { getStudyChannels, deleteChannel } from "../../service/channel";
import { studyDetail, deleteStudy } from "../../service/study";
import { getStudyRole, exitStudy } from "../../service/studyParticipant";
import { getCTInfo } from "../../service/codingTest";

import IconHome from "../Icons/IconHome";
import IconEvent from "../Icons/IconEvent";
import IconHash from "../Icons/IconHash";
import IconMeeting from "../Icons/IconMeeting";
import IconCode from "../Icons/IconCode";
import IconCodingTest from "../Icons/IconCodingTest";
import ModalCreateStudy from "../Modals/ModalCreateStudy";
import ModalCreateChannel from "../Modals/ModalCreateChannel";
import ModalInviteStudyFriend from "../Modals/ModalInviteStudyFriend";

import "./ChannelNav.css";

function ChannelNav({ meeting, navigationEvent }) {
  const navigate = useNavigate();
  const { studyId } = useParams();
  const [role, setRole] = useState("");
  const [studyname, setStudyname] = useState("");
  const [channelList, setChannelList] = useState([]);
  const [channelAddEvent, setChannelEvent] = useState(false);
  const [selectedChannelId, setSelectedChannelId] = useState("0");
  const [selectedChannelName, setSelectedChannelName] = useState("채널");
  const [selectedChannelType, setSelectedChannelType] = useState("");
  const [isCTFinshied, setIsCTFinished] = useState(false);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isStudyModalOpen, setIsStudyModalOpen] = useState(false);
  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false);
  const [isOptionsModalOpen, setIsOptiosModalOpen] = useState(false);
  const [isChannelModalOpen, setIsChannelModalOpen] = useState(false);
  const [channelModalPos, setChannelModalPos] = useState({ x: 0, y: 0 });

  const channelModalRef = useRef(null);

  // 스터디 초대 모달 관리
  function handleOpenInviteModal() {
    setIsInviteModalOpen(true);
  }
  function handleCloseInviteModal() {
    setIsInviteModalOpen(false);
  }

  // 스터디 정보 편집 모달 관리
  function handleOpenStudyModal() {
    setIsStudyModalOpen(true);
  }
  function handleCloseStudyModal() {
    setIsStudyModalOpen(false);
  }

  // 스터디 채널 추가 모달 관리
  function handleOpenModal() {
    setIsModalOpen(true);
  }
  function handleCloseModal() {
    setIsModalOpen(false);
  }

  // 스터디 채널 편집 모달 관리
  function handleOpeEditnModal() {
    setIsEditModalOpen(true);
  }
  function handleCloseEditModal() {
    setIsEditModalOpen(false);
  }

  // 옵션 모달 관리
  const optionsModalRef = useRef(null);
  function handleToggleOptionsModal() {
    setIsOptiosModalOpen((prev) => !prev);
  }

  // 채널 모달 관리
  // 우클릭 시 모달 위치 설정
  const handleRightClick = (event, channelId, channelName, channelType) => {
    event.preventDefault();
    setChannelModalPos({
      x: event.clientX - 10,
      y: event.clientY - 10,
    });
    setSelectedChannelId(channelId);
    setSelectedChannelName(channelName);
    setIsChannelModalOpen(true);
    setSelectedChannelType(channelType);

    // 채널 타입이 코딩테스트인 경우
    if (channelType === "TEST") {
      async function fetchData(selectedChannelId) {
        const response = await getCTInfo({ channelId: selectedChannelId });
        // console.log(response);
        const currentTime = new Date();
        if (response.data.length === 0) {
          setIsCTFinished(true);
        } else {
          if (currentTime > new Date(response.data[0].endTime)) {
            setIsCTFinished(true);
          } else {
            setIsCTFinished(false);
          }
        }
      }
      fetchData(channelId);
    }
  };

  const handleMouseLeave = () => {
    setIsChannelModalOpen(false);
  };

  // 스터디 나가기
  const handleExitStudy = async (studyId, memberId) => {
    try {
      if (role == "OWNER") {
        if (!window.confirm("스터디를 삭제하시겠습니까?")) return;
        await deleteStudy(studyId);
        toast.success("스터디가 삭제되었습니다.");
        navigate("/friend");
      } else {
        if (!window.confirm("스터디를 나가시겠습니까?")) return;
        await exitStudy(studyId, memberId);
        toast.success("스터디를 나갔습니다.");
        navigate("/friend");
      }
    } catch (error) {
      console.log("스터디 나가기 실패:", error);
      toast.error("스터디를 나가지 못했습니다.");
    }
  };

  // 채널 삭제
  const handleDeleteChannel = async (studyId, channelId) => {
    const path = window.location.pathname;
    const currentChannelId = path.split("/")[3];

    if (channelId == currentChannelId) {
      toast.warn("사용중인 채널은 삭제할 수 없습니다.");
      return;
    }

    if (!window.confirm("채널을 삭제하시겠습니까?")) return;

    try {
      const response = await deleteChannel(studyId, channelId);
      toast.success("채널 삭제 성공");
      setChannelEvent(true);
    } catch (error) {
      // console.log("채널 삭제 실패", error);
      toast.error("채널 삭제 실패");
    }
  };

  const handleNavigation = (path) => {
    if (meeting) {
      // toast.warn("먼저 음성통화를 종료해주세요.");
      if (navigationEvent) {
        navigationEvent(path);
      }

      return;
    }

    navigate(path);
  };

  // 클릭 시 해당 스터디의 Home 페이지로 이동
  const handleHomeClick = () => handleNavigation(`/study/${studyId}`);

  // 클릭 시 해당 스터디의 이벤트 페이지로 이동
  const handleEventClick = () => handleNavigation(`/study/${studyId}/event`);

  // 클릭 시 특정 채널로 이동
  const handleChannelClick = (channelId) =>
    handleNavigation(`/study/${studyId}/${channelId}`);

  // 클릭 시 옵션 모달 닫기
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (optionsModalRef.current) {
        setIsOptiosModalOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  useEffect(() => {
    getStudyChannels(studyId)
      .then((data) => {
        setChannelList(data);
        setChannelEvent(false);
      })
      .catch((error) => {
        console.log(error.message);
        // alert("채널 목록을 불러오는 데 실패했습니다.");
      });
  }, [channelAddEvent, studyId, isChannelModalOpen]);

  useEffect(() => {
    studyDetail(studyId)
      .then((data) => {
        setStudyname(data.data.studyName);
      })
      .catch((error) => {
        console.log(error.message);
        // alert("스터디 정보를 불러오는 데 실패했습니다.");
      });
  }, [studyId, isStudyModalOpen]);

  useEffect(() => {
    const fetchStudyRole = async () => {
      try {
        const response = await getStudyRole(studyId);
        setRole(response.data.role); // role 상태 업데이트
      } catch (error) {
        console.log("스터디 역할 불러오기 실패:", error);
      }
    };

    fetchStudyRole();
  }, [studyId]);

  return (
    <>
      {/* 스터디 채널 추가 모달 */}
      {isModalOpen && (
        <ModalCreateChannel
          open={isModalOpen}
          onClose={handleCloseModal}
          setChannelEvent={setChannelEvent}
          role={role}
        />
      )}
      {/* 스터디 채널 편집 모달 */}
      {isEditModalOpen && (
        <ModalCreateChannel
          open={isEditModalOpen}
          onClose={handleCloseEditModal}
          setChannelEvent={setChannelEvent}
          channelId={selectedChannelId}
          name={selectedChannelName}
          role={role}
        />
      )}
      {/* 스터디 초대 모달 */}
      {isInviteModalOpen && (
        <ModalInviteStudyFriend
          studyId={studyId}
          onClose={handleCloseInviteModal}
        />
      )}
      {/* 스터디 정보 편집 모달 */}
      {isStudyModalOpen && (
        <ModalCreateStudy
          onClose={handleCloseStudyModal}
          studyId={studyId}
          setStudyname={setStudyname}
        />
      )}
      <section className="channel-nav-container">
        <div className="study-name-container">
          <p>{studyname}</p>
          {/* <button onClick={handleOpenInviteModal}>+</button>
          <button onClick={handleOpenModal}>+</button> */}
          {!isOptionsModalOpen && (
            <button onClick={handleToggleOptionsModal}>⋮</button>
          )}

          {/* 옵션 모달 */}
          <div
            ref={optionsModalRef}
            className={`options-modal ${isOptionsModalOpen ? "open" : ""}`}
          >
            <div className="option" onClick={handleOpenInviteModal}>
              <p>초대하기</p>
            </div>
            {role === "OWNER" ? (
              <div className="option" onClick={handleOpenStudyModal}>
                <p>스터디 정보 편집</p>
              </div>
            ) : null}
            <div className="option" onClick={handleOpenModal}>
              <p>채널 추가</p>
            </div>
            <div
              className="option"
              onClick={() =>
                handleExitStudy(
                  studyId,
                  sessionStorage.getItem("userData.memberId")
                )
              }
            >
              {role === "OWNER" ? <p>스터디 삭제</p> : <p>스터디 나가기</p>}
            </div>
          </div>
        </div>

        <div className="channel-container" onClick={() => handleHomeClick()}>
          <div className="hover">
            <div className="hover-icon">
              <IconHome />
            </div>
            <p>Home</p>
          </div>
        </div>
        <div
          className="channel-container"
          onClick={() => handleEventClick()} // 클릭 시 상세 페이지 이동
        >
          <div className="hover">
            <div className="hover-icon">
              <IconEvent />
            </div>
            <p>Events</p>
          </div>
        </div>
        <hr />
        {channelList.map((channel, index) => (
          <div
            className="channel-container"
            key={index}
            onClick={() => handleChannelClick(channel.channelId)}
            onContextMenu={(event) =>
              handleRightClick(
                event,
                channel.channelId,
                channel.channelName,
                channel.channelType
              )
            }
          >
            <div className="hover">
              <div className="channel-icon">
                {channel.channelType === "CHAT" && <IconHash />}
                {channel.channelType === "MEETING" && <IconMeeting />}
                {channel.channelType === "CODE" && <IconCode />}
                {channel.channelType === "TEST" && <IconCodingTest />}
              </div>
              <p>{channel.channelName}</p>
            </div>
          </div>
        ))}

        {/* 채널 모달 */}
        <div
          ref={channelModalRef}
          className={`channel-modal ${isChannelModalOpen ? "open" : ""}`}
          style={{
            left: `${channelModalPos.x}px`,
            top: `${channelModalPos.y}px`,
          }}
          onMouseLeave={handleMouseLeave}
        >
          <div className="option" onClick={handleOpeEditnModal}>
            <p>채널 이름 수정</p>
          </div>
          {selectedChannelType !== "TEST" && (
            <div
              className="option"
              onClick={() => handleDeleteChannel(studyId, selectedChannelId)}
            >
              <p>채널 삭제</p>
            </div>
          )}
          {selectedChannelType == "TEST" && isCTFinshied && (
            <div
              className="option"
              onClick={() => handleDeleteChannel(studyId, selectedChannelId)}
            >
              <p>채널 삭제</p>
            </div>
          )}
        </div>
      </section>
    </>
  );
}

export default ChannelNav;
