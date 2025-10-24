import axios from "axios";
import BASE_URL from "./baseUrl";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import { createClient } from "./http";
import { debounce } from "lodash";

// SSE 엔드포인트
const SSE_URL = `${BASE_URL}/notifications/subscribe`;

const HEARTBEAT_INTERVAL = 30000; // 서버 heartbeat 간격 예상 (30초)
const HEARTBEAT_TIMEOUT = HEARTBEAT_INTERVAL + 10000; // 40초 이상 미수신 시 재연결

// SSE 연결 함수
export const initSSE = () => {
  const token = sessionStorage.getItem("accessToken");
  if (!token) {
    console.error("SSE 연결 실패: Access Token이 없습니다.");
    return;
  }

  let lastHeartbeat = Date.now();
  let reconnectTimeout = null;

  // 재연결 스케줄링 함수
  const scheduleReconnect = () => {
    if (reconnectTimeout) clearTimeout(reconnectTimeout);
    reconnectTimeout = setTimeout(() => {
      console.log("🔄 재연결 시도 중...");
      connectSSE();
    }, 5000); // 5초 후 재연결 시도
  };

  // 실제 SSE 연결 함수
  const connectSSE = () => {
    // 저장된 마지막 notificationId(lastEventId)를 헤더에 반영 (없으면 빈 문자열)
    const lastEventId = sessionStorage.getItem("lastEventId") || "";
    fetchEventSource(SSE_URL, {
      method: "GET",
      headers: {
        access: token,
        "Content-Type": "text/event-stream",
        "Last-Event-ID": lastEventId,
      },
      onopen(response) {
        if (response.ok && response.status === 200) {
          console.log("✅ SSE 연결이 열렸습니다.");
          lastHeartbeat = Date.now();
        } else {
          console.error("SSE 연결 실패:", response.statusText);
          scheduleReconnect();
        }
      },
      onmessage(event) {
        // heartbeat 이벤트 감지: 서버가 event: heartbeat 로 전송할 경우
        if (event.event === "heartbeat") {
          lastHeartbeat = Date.now();
          console.log("💓 Heartbeat 수신:", event.data);
          return;
        }
        console.log("📩 수신된 알림 데이터:", event.data);
        try {
          // 서버에서 보내는 데이터가 JSON 형식이라고 가정
          const data = JSON.parse(event.data);
          console.log("서버에서 보내는 데이터: ", data);
          // dummy 플래그가 true면 더미 이벤트로 간주하여 무시
          if (data.dummy === true) {
            console.log("Dummy 이벤트 수신. 무시합니다.");
            // return;
          }
          // notificationId가 존재하면 실제 알림 이벤트로 처리
          if (data.notificationId) {
            sessionStorage.setItem(
              "lastEventId",
              data.notificationId.toString()
            );
            sessionStorage.setItem("userData.hasNewNotifications", "true");
            window.dispatchEvent(new Event("notificationsUpdated"));
          } else {
            console.log("notificationId가 없는 이벤트는 무시합니다.");
          }
        } catch (e) {
          // JSON 파싱 실패 시(dummy 문자열 등) 무시
          console.log("JSON 파싱 실패. 이벤트 무시:", event.data);
        }
      },
      onclose() {
        console.log("SSE 연결이 종료되었습니다.");
        scheduleReconnect();
      },
      onerror(err) {
        console.error("❌ SSE 연결 오류:", err);
        scheduleReconnect();
      },
    });
  };

  // HEARTBEAT_TIMEOUT 이상 미수신 시 재연결
  setInterval(() => {
    if (Date.now() - lastHeartbeat > HEARTBEAT_TIMEOUT) {
      console.error("❌ Heartbeat timeout 발생. 재연결합니다.");
      scheduleReconnect();
    }
  }, HEARTBEAT_INTERVAL);

  // 최초 연결 실행
  connectSSE();
};

// 회원가입
export const signup = async (username, password, nickname) => {
  try {
    const response = await axios.post(`${BASE_URL}/members/signup`, {
      username,
      password,
      nickname,
    });
    return { success: true, data: response.data };
  } catch (error) {
    const errorMessage = error.response?.data?.message || "회원가입 실패";
    alert(`${errorMessage}`);
    console.error("에러:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "회원가입 실패",
    };
  }
};

// 로그인
export const login = debounce(
  async (username, password) => {
    try {
      const response = await axios.post(`${BASE_URL}/login`, {
        username,
        password,
      });

      console.log(response);

      // Access Token을 sessionStorage에 저장
      const accessToken = response.headers["access"];
      if (accessToken) {
        sessionStorage.setItem("accessToken", accessToken);
      } else {
        throw new Error("Access Token이 응답에 포함되지 않았습니다.");
      }

      const userData = await myInfo();

      // 🔥 로그인 성공 후 SSE 연결 실행
      initSSE();

      return { success: true, data: userData.data, response };
    } catch (error) {
      const errorMessage =
        error.response?.data?.message ||
        "아이디 혹은 비밀번호가 일치하지 않습니다.";
      alert(errorMessage);
      console.error("에러:", error.response?.data);

      return {
        success: false,
        message: errorMessage || "로그인 실패",
      };
    }
  },
  1000,
  { leading: true, trailing: false }
); // 첫 클릭은 즉시 실행, 이후 1초 동안 추가

// 로그아웃
export const logout = async (memberId) => {
  try {
    sessionStorage.clear();
    // const token = sessionStorage.getItem("accessToken");

    const response = await axios.post(
      `${BASE_URL}/logout`
      // 필요한 경우 memberId와 헤더 추가
    );

    return { success: true, data: response.data, status: response.status };
  } catch (error) {
    const errorMessage =
      error.response?.data?.message || "로그아웃에 실패했습니다.";
    alert(`${errorMessage}`);
    return {
      success: false,
      message: error.response?.data?.message || "로그아웃 실패",
    };
  }
};

export const myInfo = async () => {
  const client = createClient();
  const response = await client.get(`members`, {});
  console.log(response);
  return { success: true, data: response.data, response };
};

export const initMember = async () => {
  const userData = await myInfo();

  sessionStorage.setItem("userData.createdAt", userData.data.createdAt);
  sessionStorage.setItem("userData.interests", userData.data.interests);
  sessionStorage.setItem("userData.isAlarmOn", userData.data.isAlarmOn);
  sessionStorage.setItem("userData.languages", userData.data.languages);
  sessionStorage.setItem("userData.memberId", userData.data.memberId);
  sessionStorage.setItem("userData.nickname", userData.data.nickname);
  sessionStorage.setItem("userData.profileImage", userData.data.profileImage);
  sessionStorage.setItem("userData.role", userData.data.role);
  sessionStorage.setItem("userData.status", userData.data.status);
  sessionStorage.setItem("userData.username", userData.data.username);
  sessionStorage.setItem("userData.uniqueId", userData.data.uniqueId);
  sessionStorage.setItem("userData.interests", userData.data.interests);
  sessionStorage.setItem("userData.languages", userData.data.languages);

  initSSE();
  return { success: true, data: userData.data };
};

// 회원 상태 변경
export const updateMemberStatus = async (status) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) throw new Error("Access Token이 없습니다.");

    const response = await axios.patch(
      `${BASE_URL}/members/status`,
      { status }, // JSON 요청 본문
      {
        headers: {
          access: token, // JWT 토큰 포함
          "Content-Type": "application/json", // JSON 요청임을 명시
        },
      }
    );

    console.log("✅ 회원 상태 변경 성공:", response.data);
    return { success: true, data: response.data };
  } catch (error) {
    console.error("❌ 회원 상태 변경 실패:", error.response?.data);
    return {
      success: false,
      message: error.response?.data?.message || "회원 상태 변경 실패",
    };
  }
};

// 프로필 이미지 수정 함수
export const updateProfileImage = async (imageFile) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) throw new Error("Access Token이 없습니다.");

    // 이미지 검증 실행
    await validateImage(imageFile);

    // FormData 생성
    const formData = new FormData();
    formData.append("image", imageFile);

    // API 요청
    const response = await axios.patch(
      `${BASE_URL}/members/profile-image`,
      formData,
      {
        headers: {
          access: token, // JWT 토큰 포함
          "Content-Type": "multipart/form-data", // 파일 업로드를 위한 헤더
        },
      }
    );

    console.log("✅ 프로필 이미지 수정 성공:", response.data);
    return { success: true, data: response.data };
  } catch (error) {
    console.error("❌ 프로필 이미지 수정 실패:", error.message);
    return {
      success: false,
      message: error.message || "프로필 이미지 수정 실패",
    };
  }
};

export const validateImage = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const arrayBuffer = e.target.result;
      const uint8Array = new Uint8Array(arrayBuffer);
      const hex = Array.from(uint8Array)
        .map((b) => b.toString(16).padStart(2, "0"))
        .join("");

      if (!isValidImageSignature(hex)) {
        reject(new Error("유효한 이미지 파일이 아닙니다."));
      } else {
        resolve();
      }
    };
    reader.readAsArrayBuffer(file);
  });
};

export const updateNickname = async (nickname) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      throw new Error("❌ Access Token이 없습니다. 로그인 상태를 확인하세요.");
    }

    const response = await axios.patch(
      `${BASE_URL}/members/nickname`,
      { nickname },
      {
        headers: {
          access: token, // JWT 토큰 포함
          "Content-Type": "application/json", // JSON 요청임을 명시
        },
      }
    );

    console.log("✅ 닉네임 변경 성공:", response.status, response.data);

    // 닉네임 변경 후 sessionStorage 업데이트
    const newNickname = response.data?.nickname || nickname;
    sessionStorage.setItem("userData.nickname", newNickname);

    return { success: true, data: response.data };
  } catch (error) {
    console.error("❌ 닉네임 변경 요청 실패:", error);
    return { success: false, message: "닉네임 변경 실패" };
  }
};

// 관심사 변경
export const updateInterests = async (interests) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      throw new Error("❌ Access Token이 없습니다. 로그인 상태를 확인하세요.");
    }

    console.log("🔍 관심사 변경 요청:", interests);

    const response = await axios.patch(
      `${BASE_URL}/members/interests`,
      { interests }, // JSON 요청 본문
      {
        headers: {
          access: token, // JWT 토큰 포함
          "Content-Type": "application/json", // JSON 요청임을 명시
        },
      }
    );
    console.log("✅ 관심사 변경 성공:", response.status, response.data);

    // 변경된 관심사를 sessionStorage에 저장
    sessionStorage.setItem("userData.interests", JSON.stringify(interests));

    return { success: true, data: response.data };
  } catch (error) {
    console.error("❌ 관심사 변경 요청 실패:", error);
    return { success: false, message: "관심사 변경 실패" };
  }
};

const isValidImageSignature = (hex) => {
  const imageSignatures = [
    "ffd8ffe0", // JPEG
    "ffd8ffe1", // JPEG
    "ffd8ffe2", // JPEG
    "ffd8ffe3", // JPEG
    "89504e47", // PNG
  ];
  return imageSignatures.some((sig) => hex.startsWith(sig));
};

export const updateLanguages = async (languages) => {
  try {
    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      throw new Error("❌ Access Token이 없습니다. 로그인 상태를 확인하세요.");
    }

    console.log("🔍 주력 언어 변경 요청:", languages);

    const response = await axios.patch(
      `${BASE_URL}/members/languages`,
      { languages }, // JSON 요청 본문
      {
        headers: {
          access: token, // JWT 토큰 포함
          "Content-Type": "application/json", // JSON 요청임을 명시
        },
      }
    );

    console.log("✅ 주력 언어 변경 성공:", response.status, response.data);

    // 변경된 주력 언어를 sessionStorage에 저장
    sessionStorage.setItem("userData.languages", JSON.stringify(languages));

    return { success: true, data: response.data };
  } catch (error) {
    console.error("❌ 주력 언어 변경 요청 실패:", error);
    return { success: false, message: "주력 언어 변경 실패" };
  }
};
