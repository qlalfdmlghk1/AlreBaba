import React, { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import mainImg from "../../assets/images/main-img.jpg";
import channelImg from "../../assets/images/channel-img.png";
import ModalCreateStudy from "../Modals/ModalCreateStudy";
import { myStudy } from "/src/service/study";
import IconHorizotalDivider from "../Icons/IconHorizotalDivider";
import IconAddStudy from "../Icons/IconAddStudy";
import "./ChannelStudyBar.css";

function ChannelStudyBar({ studyId, studyAccepted, meeting, navigationEvent }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [studyData, setStudyData] = useState([]); // 스터디 데이터 목록
  const studyDataRef = useRef([]);
  const navigate = useNavigate();

  // API 호출 : 스터디 목록 가져오기
  const fetchStudies = async () => {
    const response = await myStudy();
    if (response.success) {
      studyDataRef.current = response.data;
      setStudyData([...response.data]); // 새로운 데이터만 업데이트
    }
  };

  useEffect(() => {
    fetchStudies();
  }, [studyAccepted]);

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => setIsModalOpen(false);

  // 클릭 시 해당 스터디의 상세 페이지로 이동
  const handleMyPageClick = () => {
    if (meeting) {
      if (navigationEvent) {
        navigationEvent("/friend");
      }

      return;
    }
    navigate("/friend");
  };

  // 클릭 시 해당 스터디의 상세 페이지로 이동
  const handleStudyClick = (studyId) => {
    if (meeting) {
      if (navigationEvent) {
        navigationEvent(`/study/${studyId}`);
      }

      return;
    }
    navigate(`/study/${studyId}`);
  };

  return (
    <div className="channel-bar">
      <div className="home-img" onClick={() => handleMyPageClick()}>
        <img src={mainImg} alt="home image" />
      </div>
      <IconHorizotalDivider />

      {/* 사용자의 스터디 개수만큼 동적으로 이미지 표시 */}
      <div className="channel-img-container">
        {studyData.map((study, index) => (
          <div
            className="channel-img"
            key={index}
            onClick={() => handleStudyClick(study.studyId)} // 클릭 시 상세 페이지 이동
            style={{ cursor: "pointer" }}
          >
            <img src={study.imageUrl} alt={`study ${index}`} loading="lazy" />
          </div>
        ))}
      </div>

      <button className="channel-add-btn" onClick={openModal}>
        <IconAddStudy />
      </button>
      {isModalOpen && <ModalCreateStudy onClose={closeModal} />}
    </div>
  );
}

export default ChannelStudyBar;
