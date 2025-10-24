import axios from "axios";
import { createClient } from "./http";

const STUDY_PARTICIPANT_BASE_URL = `/studies`;

// 스터디 초대 요청 함수
export const inviteStudyParticipant = async (studyId, inviteeId) => {
  const client = createClient();
  try {
    const response = await client.post(
      `${STUDY_PARTICIPANT_BASE_URL}/${studyId}/members/${inviteeId}`
    );

    alert("초대 요청을 보냈습니다!");
    return response.data;
  } catch (error) {
    if (error.response) {
      const { status, data } = error.response;
      console.log(status);
      console.log(data);
      // 서버에서 이미 초대된 경우의 응답을 처리
      if (status === 400 && data?.message?.includes("이미 요청된")) {
        alert("이미 초대 요청을 보냈습니다.");
        return null;
      }
      console.error("스터디 참가자 초대 요청 실패:", error);
    } else {
      console.error("네트워크 오류 또는 서버 응답 없음:", error);
    }
    throw error;
  }
};

// 스터디 초대 요청 수락
export const acceptStudyInvitation = async (studyId, notificationId) => {
  const client = createClient();
  try {
    const response = await client.patch(
      `${STUDY_PARTICIPANT_BASE_URL}/${studyId}/members/accept?notification-id=${notificationId}`
    );

    alert("스터디 초대 요청을 수락했습니다.");
    return response.data;
  } catch (error) {
    if (error.response) {
      const { status, data } = error.response;
      console.log(status);
      console.log(data);

      // 이미 가입된 경우의 응답 처리
      if (status === 400 && data?.message?.includes("이미 참가한 스터디")) {
        alert("이미 참가한 스터디입니다.");
        return null;
      }

      // 초대가 존재하지 않는 경우 처리
      if (status === 404 && data?.message?.includes("초대가 존재하지 않음")) {
        alert("해당 스터디 초대 요청이 존재하지 않습니다.");
        return null;
      }

      console.error("스터디 초대 요청 수락 실패:", error);
    } else {
      console.error("네트워크 오류 또는 서버 응답 없음:", error);
    }
    throw error;
  }
};

// 스터디 초대 요청 거절
export const rejectStudyInvitation = async (studyId, notificationId) => {
  const client = createClient();
  try {
    const response = await client.delete(
      `${STUDY_PARTICIPANT_BASE_URL}/${studyId}/members/reject?notification-id=${notificationId}`
    );

    alert("스터디 초대 요청을 거절했습니다.");
    return response.data;
  } catch (error) {
    if (error.response) {
      const { status, data } = error.response;
      console.log(status);
      console.log(data);

      // 초대 요청이 존재하지 않을 경우 처리
      if (status === 404 && data?.message?.includes("초대가 존재하지 않음")) {
        alert("해당 스터디 초대 요청이 존재하지 않습니다.");
        return null;
      }

      console.error("스터디 초대 요청 거절 실패:", error);
    } else {
      console.error("네트워크 오류 또는 서버 응답 없음:", error);
    }
    throw error;
  }
};

// 스터디 참가자 목록 조회
export const getStudyParticipants = async (studyId) => {
  const client = createClient();
  try {
    const response = await client.get(
      `${STUDY_PARTICIPANT_BASE_URL}/${studyId}/members`
    );

    // status 순서 지정: ONLINE → NO_INTERFERENCE → OFF_LINE → NO_INTERFERENCE
    const statusOrder = [
      "ONLINE",
      "ON_ANOTHER_BUSINESS",
      "OFF_LINE",
      "NO_INTERFERENCE",
    ];

    // 주어진 status 순서대로 정렬된 데이터 추출
    const sortedData = statusOrder
      .map((status) => response.data.find((entry) => entry.status === status)) // 해당 status를 가진 데이터 찾기
      .filter((entry) => entry !== undefined); // undefined 값 제거

    // members 배열만 추출하여 합치기
    const mergedMembers = sortedData
      .map((entry) => entry.members || []) // entry.members가 undefined이면 빈 배열 반환
      .flat(); // 배열들을 하나로 합치기
    // console.log("합쳐진 members 배열:", mergedMembers);
    return mergedMembers;
  } catch (error) {
    if (error.response) {
      const { status, data } = error.response;
      console.log(status);
      console.log(data);

      // 해당 스터디가 존재하지 않을 경우 처리
      if (status === 404 && data?.message?.includes("스터디를 찾을 수 없음")) {
        alert("해당 스터디를 찾을 수 없습니다.");
        return null;
      }

      console.error("스터디 참가자 목록 조회 실패:", error);
    } else {
      console.error("네트워크 오류 또는 서버 응답 없음:", error);
    }
    throw error;
  }
};

// 스터디 참가자 정보(ROLE) 조회
export const getStudyRole = async (studyId) => {
  const client = createClient();

  const response = await client.get(
    `${STUDY_PARTICIPANT_BASE_URL}/${studyId}/members/role`
  );

  return response;
};

// 스터디 나가기 또는 참가자 내보내기
export const exitStudy = async (studyId, memberId) => {
  const client = createClient();

  const response = await client.delete(
    `${STUDY_PARTICIPANT_BASE_URL}/${studyId}/members/${memberId}`
  );
  return response.data;
};
