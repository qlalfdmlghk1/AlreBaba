import axios from "axios";
import BASE_URL from "./baseUrl";

const EVENT_BASE_URL = `${BASE_URL}/studies`;

// 토큰 가져오기
const getToken = () => {
  const token = sessionStorage.getItem("accessToken");
  if (!token) throw new Error("Access Token이 없습니다.");
  return token;
};

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: EVENT_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터: 모든 요청에 자동으로 토큰 추가
apiClient.interceptors.request.use((config) => {
  config.headers.access = getToken();
  return config;
});

// 이벤트 조회
export const getEvents = async (date) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) throw new Error("Access Token이 없습니다.");

    const path = window.location.pathname;
    const studyId = path.split("/")[2];
    const formattedDate = `${date.getFullYear()}-${String(
      date.getMonth() + 1
    ).padStart(2, "0")}`;

    const response = await axios.get(
      `${EVENT_BASE_URL}/${studyId}/events?date=${formattedDate}`,
      {
        headers: {
          access: token,
          "Content-Type": "application/json",
        },
      }
    );

    return { success: true, data: response.data };
  } catch (error) {
    // console.error("에러:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "이벤트 조회에 실패하였습니다.",
    };
  }
};

// 이벤트 상세 조회
export const getEvent = async (eventId) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) throw new Error("Access Token이 없습니다.");

    const path = window.location.pathname;
    const studyId = path.split("/")[2];

    const response = await axios.get(
      `${EVENT_BASE_URL}/${studyId}/events/${eventId}`,
      {
        headers: {
          access: token,
          "Content-Type": "application/json",
        },
      }
    );

    return { success: true, data: response.data };
  } catch (error) {
    console.error("에러:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "이벤트 조회에 실패하였습니다.",
    };
  }
};

// 이벤트 생성
export const createEvent = async (eventData) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) throw new Error("Access Token이 없습니다.");

    const path = window.location.pathname;
    const studyId = path.split("/")[2];

    const response = await axios.post(
      `${EVENT_BASE_URL}/${studyId}/events`,
      eventData,
      {
        headers: {
          access: token, //  헤더에 Access Token 추가
          "Content-Type": "application/json", //  JSON 요청임을 명시
        },
      }
    );

    return { success: true, data: response.data };
  } catch (error) {
    const errorData = error.response?.data || {};
    const errorKey = ["startTime", "eventName", "description"].find(
      (key) => errorData[key]
    );
    const errorMessage = errorKey
      ? errorData[errorKey]
      : "이벤트 생성에 실패하였습니다.";

    return {
      success: false,
      message: errorMessage,
      error: errorKey,
    };
  }
};

// 이벤트 수정
export const updateEvent = async (eventId, eventData) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) throw new Error("Access Token이 없습니다.");

    const path = window.location.pathname;
    const studyId = path.split("/")[2];

    const response = await axios.patch(
      `${EVENT_BASE_URL}/${studyId}/events/${eventId}`,
      eventData,
      {
        headers: {
          access: token, // 헤더에 Access Token 추가
          "Content-Type": "application/json", // JSON 요청임을 명시
        },
      }
    );

    return { success: true, data: response.data };
  } catch (error) {
    // console.error("에러:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "이벤트 수정에 실패하였습니다.",
    };
  }
};

// 이벤트 삭제
export const deleteEvent = async (eventId) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) throw new Error("Access Token이 없습니다.");

    const path = window.location.pathname;
    const studyId = path.split("/")[2];

    const response = await axios.delete(
      `${EVENT_BASE_URL}/${studyId}/events/${eventId}`,
      {
        headers: {
          access: token, // 헤더에 Access Token 추가
          "Content-Type": "application/json", // JSON 요청임을 명시
        },
      }
    );

    return { success: true, data: response.data };
  } catch (error) {
    console.error("에러:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "이벤트 삭제에 실패하였습니다.",
    };
  }
};
