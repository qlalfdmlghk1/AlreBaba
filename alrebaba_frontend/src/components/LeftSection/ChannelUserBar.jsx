import { useParams } from "react-router-dom";
import "./ChannelUserBar.css";
import UserBar from "./UserBar";
import React from "react";
import ChannelNav from "./ChannelNav";

function ChannelUserBar({ meeting, navigationEvent }) {
  const { studyId } = useParams();

  return (
    <div className="left-sidebar-container">
      {meeting ? (
        <ChannelNav
          studyId={studyId}
          meeting={meeting}
          navigationEvent={navigationEvent}
        />
      ) : (
        <ChannelNav studyId={studyId} meeting={meeting} />
      )}

      <UserBar meeting={meeting} />
    </div>
  );
}

export default ChannelUserBar;
