import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import TitleBar from "../components/TitleBar";
import InnerHeaderBase from "../components/InnerHeader/InnerHeaderBase";
import InnerFooter from "../components/InnerFooter";
import ChannelStudyBar from "../components/LeftSection/ChannelStudyBar";
import Chatting from "../components/Chatting";
import WebRTC from "../components/WebRTC";
import CodeEditor from "../components/CodeEditor";
import CodingTestStartPage from "./CodingTestStartPage";
import FriendStatusBar from "../components/RightSection/FriendStatusBar";
import ChannelUserBar from "../components/LeftSection/ChannelUserBar";
import { getStudyChannels } from "../service/channel";
import "./FriendPage.css";
import "./ChannelPage.css";

function ChannelPage() {
  const { studyId } = useParams();
  const { channelId } = useParams();
  const [channelList, setChannelList] = useState({});
  const [targetPath, setTargetPath] = useState("");
  const [sendEvent, setSendEvent] = useState("");

  const handleNavigationEvent = (path) => {
    setTargetPath(path);
    setSendEvent(!sendEvent);
  };

  useEffect(() => {
    const fetchChannels = async () => {
      try {
        const channels = await getStudyChannels(studyId);

        const channelMap = channels.reduce((acc, channel) => {
          acc[channel.channelId] = {
            channelName: channel.channelName,
            channelType: channel.channelType,
          };
          return acc;
        }, {});

        setChannelList(channelMap);
        // console.log(channelMap);
      } catch (error) {
        console.error("채널 목록을 불러오는 데 실패했습니다.", error);
      }
    };

    if (studyId) {
      fetchChannels();
    }

    setTargetPath("");
  }, [studyId, channelId]);

  if (!channelId || !channelList[channelId]) {
    return (
      <>
        <div className="layout-container">
          <div className="left">
            <ChannelStudyBar />
            <ChannelUserBar />
          </div>
          <div className="center">
            <TitleBar />
            <InnerHeaderBase />
            <div className="channel-page-loading-container">로딩 중...</div>
            <InnerFooter />
          </div>
          <div className="right-channel">
            <FriendStatusBar studyId={studyId} />
          </div>
        </div>
      </>
    );
  }

  const { channelName, channelType } = channelList[channelId];

  const renderChannelContent = () => {
    switch (channelType) {
      case "CHAT":
        return (
          <>
            <div className="layout-container">
              <div className="left">
                <ChannelStudyBar />
                <ChannelUserBar />
              </div>
              <div className="center">
                <TitleBar identifier={"채팅"} channelName={channelName} />
                <InnerHeaderBase />
                <Chatting channelName={channelName} />
                <InnerFooter />
              </div>
              <div className="right-channel">
                <FriendStatusBar studyId={studyId} />
              </div>
            </div>
          </>
        );
      case "MEETING":
        return (
          <>
            <div className="layout-container">
              <div className="left">
                <ChannelStudyBar
                  meeting={true}
                  navigationEvent={handleNavigationEvent}
                />
                <ChannelUserBar
                  meeting={true}
                  navigationEvent={handleNavigationEvent}
                />
              </div>
              <div className="center">
                <TitleBar identifier={"음성"} channelName={channelName} />
                <WebRTC targetPath={targetPath} sendEvent={sendEvent} />
              </div>
              <div className="right-channel">
                <FriendStatusBar studyId={studyId} />
              </div>
            </div>
          </>
        );
      case "CODE":
        return (
          <>
            <div className="layout-container">
              <div className="left">
                <ChannelStudyBar />
                <ChannelUserBar />
              </div>
              <div className="center">
                <TitleBar identifier={"코드 리뷰"} channelName={channelName} />
                <CodeEditor
                  participantName={sessionStorage.getItem("userData.uniqueId")}
                  channelId={channelId}
                />
                <InnerFooter />
              </div>
              <div className="right-channel">
                <FriendStatusBar studyId={studyId} />
              </div>
            </div>
          </>
        );
      case "TEST":
        return (
          <>
            <CodingTestStartPage channelName={channelName} />
          </>
        );
      default:
        return;
    }
  };

  return <>{renderChannelContent()}</>;
}

export default ChannelPage;
