import { LocalVideoTrack, RemoteVideoTrack } from "livekit-client";
import React, { useEffect, useRef } from "react";

import "./VideoComponent.css";

interface VideoComponentProps {
  track: LocalVideoTrack | RemoteVideoTrack;
  participantIdentity: string;
  local?: boolean;
  participantName: string;
}

function VideoComponent({
  track,
  participantIdentity,
  local = false,
  participantName,
}: VideoComponentProps) {
  const videoElement = useRef<HTMLVideoElement | null>(null);

  useEffect(() => {
    if (videoElement.current) {
      track.attach(videoElement.current);
    }

    return () => {
      track.detach();
    };
  }, [track]);

  return (
    <>
      <video
        ref={videoElement}
        id={track.sid}
        autoPlay
        muted
        playsInline
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          width: "100%",
          height: "100%",
          objectFit: "cover",
          borderRadius: "2vh",
        }}
      />

      <div className="participant-data">
        <p>{participantName + (local ? " (You)" : "")}</p>
      </div>
    </>
  );
}

export default VideoComponent;
