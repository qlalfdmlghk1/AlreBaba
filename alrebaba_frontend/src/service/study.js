import axios from "axios";
import BASE_URL from "./baseUrl";
import { createClient } from "./http";
import {validateImage} from "./member";

const STUDY_BASE_URL = `${BASE_URL}/studies`;

// 토큰 가져오기
const getToken = () => {
  const token = sessionStorage.getItem("accessToken");
  if (!token) {
    window.location.href = "/";
    throw new Error("Access Token이 없습니다.");
  }
  return token;
};

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: STUDY_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터: 모든 요청에 자동으로 토큰 추가
apiClient.interceptors.request.use((config) => {
  config.headers.access = getToken();
  return config;
});

// 스터디 생성
export const createStudy = async (studyName) => {
  try {
    const response = await apiClient.post("", { studyName });
    return { success: true, data: response.data };
  } catch (error) {
    console.error("에러:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "스터디 생성에 실패하였습니다.",
    };
  }
};

// 스터디 이미지 업로드
export const uploadImage = async (studyId, imageFile) => {
  try {

    validateImage(imageFile); // 이미지 유���성 검사
    
    const formData = new FormData();
    formData.append("multipartFile", imageFile);

    const response = await apiClient.post(`/${studyId}`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    return { success: true, imageUrl: response.data.imageUrl };
  } catch (error) {
    console.error("이미지 업로드 실패:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "이미지 업로드 실패",
    };
  }
};

// 내 스터디 조회
export const myStudy = async () => {
  try {
    const response = await apiClient.get("");
    return { success: true, data: response.data };
  } catch (error) {
    console.error("에러:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "내 스터디 불러오기 실패",
    };
  }
};

// 스터디 description 수정 (줄바꿈 `\n` 변환 후 전송)
export const updateStudyDescription = async (studyId, description) => {
  try {
    const requestBody = {
      description: description.replace(/\n/g, "\\n"), // 줄바꿈 변환
    };

    const response = await apiClient.patch(`/${studyId}`, requestBody);

    return {
      success: true,
      data: response.data,
      content: requestBody.description,
    };
  } catch (error) {
    console.error("❌ 업데이트 실패:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "스터디 정보 업데이트 실패",
    };
  }
};

// 스터디 name 수정
export const updateStudyName = async (studyId, studyName) => {
  try {
    const requestBody = {
      studyName,
    };

    const response = await apiClient.patch(`/${studyId}`, requestBody);

    return {
      success: true,
      data: response.data,
      content: requestBody.description,
    };
  } catch (error) {
    console.error("❌ 업데이트 실패:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "스터디 정보 업데이트 실패",
    };
  }
};

// 스터디 상세 조회
export const studyDetail = async (studyId) => {
  try {
    const response = await apiClient.get(`/${studyId}`);
    return { success: true, data: response.data };
  } catch (error) {
    console.error("에러:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "내 스터디 불러오기 실패",
    };
  }
};

// 스터디 삭제
export const deleteStudy = async (studyId) => {
  const client = createClient();

  const response = await client.delete(`${STUDY_BASE_URL}/${studyId}`);
  return response.data;
};

// OpevVidu 토큰 가져오기
export const getVideoToken = async (studyId, channelId) => {
  try {
    const response = await apiClient.post(
      `${STUDY_BASE_URL}/${studyId}/channel/${channelId}/token`,
      {}
    );

    return response.data.token;
  } catch (error) {
    throw new Error(
      `토큰 얻기 실패!: ${error.response?.data?.errorMessage || error.message}`
    );
  }
};
