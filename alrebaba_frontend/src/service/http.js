import axios from "axios";
import BASE_URL from "./baseUrl";

const HTTP_BASE_URL = `${BASE_URL}/`;
const DEFAULT_TIMEOUT = 60000;

export const createClient = () => {
  const axiosInstance = axios.create({
    baseURL: HTTP_BASE_URL,
    timeout: DEFAULT_TIMEOUT,
    withCredentials: true,
  });

  axiosInstance.interceptors.request.use(
    (config) => {
      const accessToken = sessionStorage.getItem("accessToken"); // 액세스 토큰 로드
    
      if (accessToken) {
        config.headers.access = `${accessToken}`; // Authorization 헤더 추가
      }

      return config;
    },
    (error) => Promise.reject(error)
  );

  // 응답 인터셉터 설정
  axiosInstance.interceptors.response.use(
    (response) => response, // 정상 응답은 그대로 반환
    async (error) => {
      const originalRequest = error.config;

      // 401 에러 발생 시 처리
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true; // 중복 요청 방지 플래그

        try {
          // 리프레시 토큰으로 새 액세스 토큰 요청
          const refreshResponse = await axios.post(
            `${BASE_URL}/reissue`,
            {}, // 리프레시 토큰 엔드포인트
            { withCredentials: true } // HTTP-Only 쿠키 전송
          );

          const newAccessToken = refreshResponse.headers["access"];
          sessionStorage.setItem("accessToken", newAccessToken); // 새로운 토큰 저장
          console.log("토큰 재발급 됨!!!");

          // 원래 요청에 새로운 토큰 추가
          originalRequest.headers.access = `${newAccessToken}`;
          return axiosInstance(originalRequest); // 원래 요청 재시도
        } catch (refreshError) {
          console.error("토큰 재발급 실패:", refreshError);
          // window.location.href = "/";
          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error); // 다른 에러는 그대로 전달
    }
  );

  return axiosInstance;
};


export const getAccessTokenByRefreshToken = async () => {
  const refreshResponse = await axios.post(
    `${BASE_URL}/reissue`,
    {}, // 리프레시 토큰 엔드포인트
    { withCredentials: true } // HTTP-Only 쿠키 전송
  );

  const newAccessToken = refreshResponse.headers["access"];
  sessionStorage.setItem("accessToken", newAccessToken); // 새로
}


export const httpClient = createClient();
