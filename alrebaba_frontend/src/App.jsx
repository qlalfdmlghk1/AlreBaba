import React, { useEffect } from "react";
import { Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";

import Initial from "./pages/InitialPage";
import AlertPage from "./pages/AlertPage";
import EventPage from "./pages/EventPage";
import FriendPage from "./pages/FriendPage";
import ProfilePage from "./pages/ProfilePage";
import ChannelPage from "./pages/ChannelPage";
import CodingTestPage from "./pages/CodingTestPage";
import StudyDetailPage from "./pages/StudyDetailPage";
import ProblemDetailPage from "./pages/ProblemDetailPage";
import NotFoundPage from "./pages/NotFoundPage";

import { initSSE } from "./service/member"; // SSE 연결 함수 import

import "./App.css";
import "react-toastify/dist/ReactToastify.css";

function App() {
  useEffect(() => {
    // 새로고침 후에도 sessionStorage에 accessToken이 있으면 SSE 연결 재설정
    const token = sessionStorage.getItem("accessToken");
    if (token) {
      initSSE();
    } else {
      console.log("로그인 전이므로 SSE 연결을 시작하지 않습니다.");
    }
  }, []);

  return (
    <>
      <ToastContainer
        position="top-center"
        autoClose={1500}
        hideProgressBar={true}
        theme="colored"
      />
      <Routes>
        <Route path="/" element={<Initial />} />
        <Route path="/friend" element={<FriendPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/alert" element={<AlertPage />} />
        <Route path="/study/:studyId" element={<StudyDetailPage />} />
        <Route path="/study/:studyId/event" element={<EventPage />} />
        <Route path="/study/:studyId/:channelId" element={<ChannelPage />} />
        <Route
          path="/study/:studyId/:channelId/:testId"
          element={<CodingTestPage />}
        >
          <Route path=":problemId" element={<ProblemDetailPage />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </>
  );
}

export default App;
