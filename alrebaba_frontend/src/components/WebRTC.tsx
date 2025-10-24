import {
  LocalVideoTrack,
  Participant,
  RemoteParticipant,
  RemoteTrack,
  RemoteTrackPublication,
  Room,
  RoomEvent,
} from "livekit-client";

import React, { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";
import { useNavigate, useParams } from "react-router-dom";
import { getVideoToken } from "../service/study";
import { getStudyParticipants } from "../service/studyParticipant";
import VideoComponent from "./VideoComponent";
import AudioComponent from "./AudioComponent";
import InnerHeaderBase from "./InnerHeader/InnerHeaderBase";
import InnerFooter from "./InnerFooter";
import IconMic2 from "./Icons/IconMic2";
import IconMicOff2 from "./Icons/IconMicOff2";
import IconHeadPhone2 from "./Icons/IconHeadPhone2";
import IconHeadPhoneOff2 from "./Icons/IconHeadPhoneOff2";
import IconVideo from "./Icons/IconVideo";
import IconVideoOff from "./Icons/IconVideoOff";
import IconCall from "./Icons/IconCall";
import IconFullScreen from "./Icons/IconFullScreen";

import "./WebRTC.css";

type TrackInfo = {
  trackPublication: RemoteTrackPublication;
  participantIdentity: string;
  participantName: string;
};

// let APPLICATION_SERVER_URL = "https://i12a702.p.ssafy.io:8080/api/v1/";
let LIVEKIT_URL = "wss://i12a702.p.ssafy.io:443/";

function OpenViduTest({ targetPath, sendEvent }) {
  const navigate = useNavigate();
  const [participantsMap, setParticipantsMap] = useState({});
  const [loading, setLoading] = useState(true);
  const { studyId, channelId } = useParams();

  // 마이크, 비디오, 헤드폰 상태 관리
  const [micState, setMicState] = useState(true);
  const [videoState, setVideoState] = useState(false);
  const [headPhoneState, setHeadPhoneState] = useState(true);

  const [isFullScreen, setIsFullScreen] = useState(false);
  const rtcContentRef = useRef<HTMLDivElement>(null);

  // 방, 로컬 트랙, 리모트 트랙 상태 관리
  const [room, setRoom] = useState<Room | undefined>(undefined);
  const [localTrack, setLocalTrack] = useState<LocalVideoTrack | undefined>(
    undefined
  );
  const [remoteTracks, setRemoteTracks] = useState<TrackInfo[]>([]);

  // 로컬 사용자 상태 관리
  const [userName, setUserName] = useState("");
  const [userImage, setUserImage] = useState("");

  const isDisconnectRef = useRef(false);

  useEffect(() => {
    // 첫 렌더링 시 pushState로 현재 페이지 상태를 덮어씀씀
    history.pushState(null, "", window.location.href);

    // 뒤로 가기 버튼이 눌렸을 때 처리하는 이벤트 핸들러
    const handleBack = () => {
      toast.warn("뒤로 가기가 제한되어 있습니다.", {
        autoClose: 1500,
      });
      // 다시 현재 페이지로 덮어쓰기
      history.pushState(null, "", window.location.href);
    };

    // popstate 이벤트 리스너 추가
    window.addEventListener("popstate", handleBack);

    // 컴포넌트 언마운트 시 이벤트 리스너 제거
    return () => {
      window.removeEventListener("popstate", handleBack);
    };
  }, []);

  useEffect(() => {
    if (targetPath) {
      const path = window.location.pathname;
      // console.log("WebRTC 컴포넌트에서 받은 path:", targetPath);
      // console.log(path);

      if (path == targetPath) return;

      if (loading || !room || !localTrack) {
        toast.warn("잠시후 다시 시도해 주세요.");
      } else {
        leaveRoom();
      }
    }
  }, [targetPath, sendEvent]);

  // 사용자 정보 가져오기
  async function fetchUserInfo() {
    setUserName(sessionStorage.getItem("userData.nickname") || ""); // 사용자 닉네임
    setUserImage(
      sessionStorage.getItem("userData.profileImage") ||
        "src/assets/images/profile.png"
    ); // 사용자 프로필 이미지
  }

  // 마이크 상태변경
  const toggleMicState = async () => {
    if (room) {
      if (room.localParticipant.isMicrophoneEnabled) {
        // 마이크 끄기
        await room.localParticipant.setMicrophoneEnabled(false);
      } else {
        // 마이크 켜기
        await room.localParticipant.setMicrophoneEnabled(true);
      }
    }

    setMicState((prevState) => !prevState);
  };

  // 비디오 상태변경
  const toggleVideoState = async () => {
    if (room) {
      if (room.localParticipant.isCameraEnabled) {
        // 카메라 끄기
        await room.localParticipant.setCameraEnabled(false);
        // console.log(room.localParticipant.isCameraEnabled);
      } else {
        // 카메라 켜기
        await room.localParticipant.setCameraEnabled(true);
        // console.log(room.localParticipant.isCameraEnabled);
      }
    }

    setVideoState((prevState) => !prevState);
  };

  // 헤드셋 상태변경
  const toggleHeadPhoneState = async () => {
    if (room) {
      if (room.localParticipant.isSpeaking) {
        await room.localParticipant.setIsSpeaking(false);
      } else {
        await room.localParticipant.setIsSpeaking(true);
      }
    }

    setHeadPhoneState((prevState) => !prevState);
  };

  // 전체화면 설정
  const toggleFullScreen = () => {
    if (!document.fullscreenElement && rtcContentRef.current) {
      rtcContentRef.current.requestFullscreen().then(() => {
        setIsFullScreen(true);
      });
    } else if (document.exitFullscreen) {
      document.exitFullscreen().then(() => {
        setIsFullScreen(false);
      });
    }
  };

  async function joinRoom() {
    // Initialize a new Room object
    const room = new Room();
    setRoom(room);

    // Specify the actions when events take place in the room
    // On every new Track received...
    room.on(RoomEvent.TrackSubscribed, (_track, publication, participant) => {
      setRemoteTracks((prev) => [
        ...prev,
        {
          trackPublication: publication,
          participantIdentity: participant.identity,
          participantName: userName,
        },
      ]);
    });

    // console.log(remoteTracks);

    // On every Track destroyed...
    room.on(RoomEvent.TrackUnsubscribed, (_track, publication) => {
      setRemoteTracks((prev) =>
        prev.filter(
          (track) => track.trackPublication.trackSid !== publication.trackSid
        )
      );
    });

    try {
      // 토큰 받기
      const token = await getVideoToken(studyId, channelId);

      // console.log("Token:", token);

      // Connect to the room with the LiveKit URL and the token
      await room.connect(LIVEKIT_URL, token);

      // Publish your camera and microphone
      await room.localParticipant.enableCameraAndMicrophone();
      // set camera off
      // await room.localParticipant.setCameraEnabled(false);

      setLocalTrack(
        room.localParticipant.videoTrackPublications.values().next().value
          .videoTrack
      );
    } catch (error) {
      console.log(
        "There was an error connecting to the room:",
        (error as Error).message
      );
      await leaveRoom();
    }
  }

  async function leaveRoom() {
    isDisconnectRef.current = true;
    await room?.disconnect();

    // Reset the state
    setRoom(undefined);
    setLocalTrack(undefined);
    setRemoteTracks([]);
    if (targetPath) navigate(targetPath);
    else navigate("/study/" + studyId);
  }

  useEffect(() => {
    async function initializeRoom() {
      fetchUserInfo();

      // 사용자 정보가 설정된 후 방 입장
      if (userName && !isDisconnectRef.current) {
        joinRoom();
      }
    }

    if (!room && !isDisconnectRef.current) {
      initializeRoom();
    }

    const handleTrackUpdate = () => {
      const updatedTracks: TrackInfo[] = [];

      room?.remoteParticipants.forEach((participant) => {
        participant.trackPublications.forEach((publication) => {
          if (publication.kind === "video" || publication.kind === "audio") {
            updatedTracks.push({
              trackPublication: publication,
              participantIdentity: participant.identity,
              participantName: participant.name || "사용자",
            });
          }
        });
      });

      setRemoteTracks(updatedTracks);
    };

    // 초기 상태 업데이트
    handleTrackUpdate();

    // 상태 변경 감지
    room?.on(RoomEvent.ParticipantConnected, handleTrackUpdate);
    room?.on(RoomEvent.ParticipantDisconnected, handleTrackUpdate);
    room?.on(RoomEvent.TrackSubscribed, handleTrackUpdate);
    room?.on(RoomEvent.TrackUnsubscribed, handleTrackUpdate);
    room?.on(RoomEvent.TrackMuted, handleTrackUpdate);
    room?.on(RoomEvent.TrackUnmuted, handleTrackUpdate);

    return () => {
      room?.off(RoomEvent.ParticipantConnected, handleTrackUpdate);
      room?.off(RoomEvent.ParticipantDisconnected, handleTrackUpdate);
      room?.off(RoomEvent.TrackSubscribed, handleTrackUpdate);
      room?.off(RoomEvent.TrackUnsubscribed, handleTrackUpdate);
      room?.off(RoomEvent.TrackMuted, handleTrackUpdate);
      room?.off(RoomEvent.TrackUnmuted, handleTrackUpdate);
    };
  }, [room, userName]);

  useEffect(() => {
    const fetchParticipants = async () => {
      try {
        const data = await getStudyParticipants(studyId);
        // console.log(data);

        // `username`을 key로 하고 나머지 정보를 value로 하는 객체로 변환
        const participantsMap = data?.reduce((acc, member) => {
          const { username, ...rest } = member;
          acc[username] = rest;
          return acc;
        }, {});

        setParticipantsMap(participantsMap);
      } catch (error) {
        console.error("스터디 참가자 목록을 가져오는 중 오류 발생:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchParticipants();
  }, []);

  if (loading || !room || !localTrack) {
    return (
      <div className="web-rtc">
        <InnerHeaderBase />
        <div className="web-rtc-content" ref={rtcContentRef}>
          <>
            <div>연결 중입니다. 잠시만 기다려 주세요</div>
          </>
        </div>
      </div>
    );
  }

  return (
    <>
      <div className="web-rtc">
        <InnerHeaderBase />
        <div className="web-rtc-content" ref={rtcContentRef}>
          {!room ? (
            <></>
          ) : (
            <div className="participants">
              <div
                className="participant"
                style={{ width: `${100 / remoteTracks.length}%` }}
              >
                {localTrack && room && room.localParticipant.isCameraEnabled ? (
                  <VideoComponent
                    track={localTrack}
                    participantIdentity={userName}
                    local={true}
                    participantName={userName}
                  />
                ) : (
                  <>
                    <img
                      src={userImage} // 내 이미지 경로
                      alt="Default Avatar"
                      className="participant-image"
                    />

                    <div className="participant-data">
                      <p>{userName + " (You)"}</p>
                    </div>
                  </>
                )}
              </div>

              {remoteTracks.map((remoteTrack) => {
                const participant =
                  participantsMap?.[remoteTrack.participantIdentity]; // 참가자 정보 가져오기

                return remoteTrack.trackPublication.kind === "video" ? (
                  <div
                    key={remoteTrack.trackPublication.trackSid}
                    className="participant"
                    style={{ width: `${100 / remoteTracks.length}%` }}
                    onClick={() => {
                      console.log(!remoteTrack.trackPublication.isMuted);
                    }}
                  >
                    {!remoteTrack.trackPublication!.isMuted &&
                    remoteTrack.trackPublication.videoTrack ? (
                      <VideoComponent
                        key={remoteTrack.trackPublication.trackSid}
                        track={remoteTrack.trackPublication.videoTrack!}
                        participantIdentity={
                          participant?.nickname || "알 수 없음"
                        }
                        participantName={participant?.nickname || "알 수 없음"}
                      />
                    ) : (
                      <>
                        <img
                          src={
                            participant?.profileImage || "/default-profile.png"
                          } // 기본 이미지 설정
                          alt="User Image"
                          className="participant-image"
                        />

                        <div className="participant-data">
                          <p>{participant?.nickname || "알 수 없음"}</p>
                        </div>
                      </>
                    )}
                  </div>
                ) : (
                  remoteTrack.trackPublication.audioTrack && (
                    <AudioComponent
                      key={remoteTrack.trackPublication.trackSid}
                      track={remoteTrack.trackPublication.audioTrack!}
                    />
                  )
                );
              })}

              <div className="rtc-footer">
                <div></div>
                <div className="rtc-setting">
                  {room.localParticipant.isCameraEnabled ? (
                    <div className="settings" onClick={toggleVideoState}>
                      <IconVideo />
                    </div>
                  ) : (
                    <div className="settings-off" onClick={toggleVideoState}>
                      <IconVideoOff />
                    </div>
                  )}
                  {micState ? (
                    <div className="settings" onClick={toggleMicState}>
                      <IconMic2 />
                    </div>
                  ) : (
                    <div className="settings-off" onClick={toggleMicState}>
                      <IconMicOff2 />
                    </div>
                  )}
                  {/* {headPhoneState ? (
                    <div className="settings" onClick={toggleHeadPhoneState}>
                      <IconHeadPhone2 />
                    </div>
                  ) : (
                    <div
                      className="settings-off"
                      onClick={toggleHeadPhoneState}
                    >
                      <IconHeadPhoneOff2 />
                    </div>
                  )} */}
                  <div className="disconnect" onClick={leaveRoom}>
                    <IconCall />
                  </div>
                </div>
                <div className="full-screen-icon" onClick={toggleFullScreen}>
                  <IconFullScreen />
                </div>
              </div>
            </div>
          )}
        </div>
        <InnerFooter />
      </div>
    </>
  );
}

export default OpenViduTest;
