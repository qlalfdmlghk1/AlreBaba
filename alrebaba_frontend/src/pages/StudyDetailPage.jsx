import { useParams } from "react-router-dom";
import TitleBar from "../components/TitleBar";
import ChannelStudyBar from "../components/LeftSection/ChannelStudyBar";
import StudyHome from "../components/StudyHome";
import FriendStatusBar from "../components/RightSection/FriendStatusBar";
import ChannelUserBar from "../components/LeftSection/ChannelUserBar";
import "./FriendPage.css";

function StudyDetailPage() {
  const { studyId } = useParams(); // studyId 값 가져오기

  return (
    <>
      <div className="layout-container">
        <div className="left">
          <ChannelStudyBar studyId={studyId} />
          <ChannelUserBar />
        </div>
        <div className="center">
          <TitleBar identifier={"Home"} channelName={"Home"} />
          <StudyHome studyId={studyId} />
        </div>
        <div className="right-channel">
          <FriendStatusBar studyId={studyId} />
        </div>
      </div>
    </>
  );
}

export default StudyDetailPage;
