import TitleBar from "../components/TitleBar";
import InnerFooter from "../components/InnerFooter";
import ChannelStudyBar from "../components/LeftSection/ChannelStudyBar";
import FriendStatusBar from "../components/RightSection/FriendStatusBar";
import ChannelUserBar from "../components/LeftSection/ChannelUserBar";
import Event from "../components/Event";
import "./FriendPage.css";
import { useParams } from "react-router-dom";

function EventPage() {
  const { studyId } = useParams(); // studyId 값 가져오기
  return (
    <>
      <div className="layout-container">
        <div className="left">
          <ChannelStudyBar />
          <ChannelUserBar />
        </div>
        <div className="center">
          <TitleBar identifier={"Event"} />
          <Event />
          <InnerFooter />
        </div>
        <div className="right-channel">
          <FriendStatusBar studyId={studyId} />
        </div>
      </div>
    </>
  );
}

export default EventPage;
