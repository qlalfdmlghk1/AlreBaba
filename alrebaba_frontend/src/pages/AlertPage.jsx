import React, { useEffect, useState } from "react";
import TitleBar from "../components/TitleBar";
import InnerHeaderBase from "../components/InnerHeader/InnerHeaderBase";
import InnerFooter from "../components/InnerFooter";
import ChannelStudyBar from "../components/LeftSection/ChannelStudyBar";
import StudyUserBar from "../components/LeftSection/StudyUserBar";
import Alert from "../components/Alert";
import "./FriendPage.css";

function AlertPage() {
  const [studyAccepted, setStudyAccepted] = useState(false);

  // 스터디 요청 수락
  function handleStudyAccept() {
    setStudyAccepted((prev) => !prev);
  }

  return (
    <>
      <div className="layout-container">
        <div className="left">
          <ChannelStudyBar studyAccepted={studyAccepted} />
          <StudyUserBar />
        </div>
        <div className="center">
          <TitleBar identifier={"알림"} />
          <InnerHeaderBase />
          <Alert studyAccept={handleStudyAccept} />
          <InnerFooter />
        </div>
      </div>
    </>
  );
}

export default AlertPage;
