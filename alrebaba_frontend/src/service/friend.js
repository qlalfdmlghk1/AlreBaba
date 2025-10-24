import axios from "axios";
import { myInfo } from "./member";
import BASE_URL from "./baseUrl";
import { createClient } from "./http";

const FRIEND_BASE_URL = `${BASE_URL}/friends`;

// 토큰 가져오기
const getToken = () => {
  const token = sessionStorage.getItem("accessToken");
  if (!token) throw new Error("Access Token이 없습니다.");
  return token;
};

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: FRIEND_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터: 모든 요청에 자동으로 토큰 추가
apiClient.interceptors.request.use((config) => {
  config.headers.access = getToken();
  return config;
});

// 친구 검색
// export const searchFriends = async (search, lastId, pageSize) => {
//   const client = createClient();
//   try {
//     const response = await apiClient.get("/search", {
//       params: { search, lastId, pageSize },
//     });
//     return response.data.content;
//   } catch (error) {
//     return [];
//   }
// };
export const searchFriends = async (search, lastId, pageSize) => {
  const client = createClient();
  try {
    const response = await client.get(`${FRIEND_BASE_URL}/search`, {
      params: { search, lastId, pageSize },
    });
    return response.data.content;
  } catch (error) {
    console.error("친구 검색 실패:", error);
    return [];
  }
};

// 친구 요청 보내기
// export const sendFriendRequest = async (acceptId) => {
//   try {
//     const userInfo = await myInfo();
//     console.log("[내 정보 응답]", userInfo);
//     if (!userInfo.success) {
//       console.error("[친구 요청 오류] 사용자 정보를 가져올 수 없습니다.");
//       return null;
//     }
//     const requestId = userInfo.data.memberId;
//     console.log("[친구 요청 전송] Data:", { requestId, acceptId });
//     const response = await axios.post(
//       `${FRIEND_BASE_URL}/request`,
//       { requestId, acceptId },
//       {
//         headers: { access: getToken() },
//       }
//     );
//     console.log(
//       "[친구 요청 응답] Status:",
//       response.status,
//       "Data:",
//       response.data
//     );
//     return response.data;
//     // return response;
//   } catch (error) {
//     console.error(
//       "[친구 요청 오류] Status:",
//       error.response?.status,
//       "Message:",
//       error.message
//     );
//     return null;
//   }
// };

export const sendFriendRequest = async (acceptId) => {
  const client = createClient();

  try {
    const userInfo = await myInfo();
    console.log("[내 정보 응답]", userInfo);

    if (!userInfo.success) {
      console.error("[친구 요청 오류] 사용자 정보를 가져올 수 없습니다.");
      return null;
    }

    const requestId = userInfo.data.memberId;
    console.log("[친구 요청 전송] Data:", { requestId, acceptId });

    const response = await client.post(`${FRIEND_BASE_URL}/request`, {
      requestId,
      acceptId,
    });

    console.log(
      "[친구 요청 응답] Status:",
      response.status,
      "Data:",
      response.data
    );

    return response.data;
  } catch (error) {
    console.error(
      "[친구 요청 오류] Status:",
      error.response?.status || "N/A",
      "Message:",
      error.message
    );
    return null;
  }
};

// 보낸 친구 요청 목록 조회
// export const getSentFriendRequests = async (
//   memberId,
//   lastId = null,
//   pageSize = 10
// ) => {
//   try {
//     const response = await apiClient.get("/requests-sent", {
//       params: { memberId, lastId, pageSize },
//     });
//     return response.data;
//   } catch (error) {
//     console.error("[보낸 친구 요청 조회 오류]:", error);
//     return [];
//   }
// };
export const getSentFriendRequests = async (
  memberId,
  lastId = null,
  pageSize = 10
) => {
  const client = createClient();
  try {
    const response = await client.get(`${FRIEND_BASE_URL}/requests-sent`, {
      params: { memberId, lastId, pageSize },
    });
    return response.data;
  } catch (error) {
    console.error("[보낸 친구 요청 조회 오류]:", error);
    return [];
  }
};

// 받은 친구 요청 목록 조회
// export const getReceivedFriendRequests = async (
//   pageSize = 20,
//   lastId = null
// ) => {
//   try {
//     const params = { pageSize };
//     if (lastId) params.lastId = lastId;

//     const response = await apiClient.get("/requests-received", {
//       params: { pageSize, lastId },
//     });

//     console.log("[받은 친구 요청 목록]:", response.data);
//     return Array.isArray(response.data.content) ? response.data : []; // 배열이 아니면 빈 배열 반환
//   } catch (error) {
//     console.error("[받은 친구 요청 조회 오류]:", error);
//     return [];
//   }
// };
export const getReceivedFriendRequests = async (
  pageSize = 20,
  lastId = null
) => {
  const client = createClient();
  try {
    const params = { pageSize };
    if (lastId) params.lastId = lastId;

    const response = await client.get(`${FRIEND_BASE_URL}/requests-received`, {
      params,
    });

    console.log("[받은 친구 요청 목록]:", response.data);
    return Array.isArray(response.data.content) ? response.data : []; // 배열이 아니면 빈 배열 반환
  } catch (error) {
    console.error("[받은 친구 요청 조회 오류]:", error);
    return [];
  }
};

// 친구 상태 변경
// export const acceptOrBlockFriend = async (friendId, status) => {
//   try {
//     let memberId = Number(sessionStorage.getItem("userData.memberId"));
//     const payload = {
//       memberId,
//       friendId,
//       status, // "FOLLOWING" 또는 "BANNED"
//     };
//     console.log("[친구 상태 변경 요청] Data:", payload);

//     const response = await apiClient.patch("", payload);

//     console.log(
//       "[친구 상태 변경 응답] Status:",
//       response.status,
//       "Data:",
//       response.data
//     );
//     return response.data;
//   } catch (error) {
//     console.error(
//       "[친구 상태 변경 오류] Status:",
//       error.response?.status,
//       "Message:",
//       error.message
//     );
//     return null;
//   }
// };
export const acceptOrBlockFriend = async (friendId, status) => {
  const client = createClient();

  try {
    let memberId = Number(sessionStorage.getItem("userData.memberId"));
    const payload = {
      memberId,
      friendId,
      status, // "FOLLOWING" 또는 "BANNED"
    };

    console.log("[친구 상태 변경 요청] Data:", payload);

    const response = await client.patch(`${FRIEND_BASE_URL}`, payload);

    console.log(
      "[친구 상태 변경 응답] Status:",
      response.status,
      "Data:",
      response.data
    );

    return response.data;
  } catch (error) {
    console.error(
      "[친구 상태 변경 오류] Status:",
      error.response?.status || "N/A",
      "Message:",
      error.message
    );
    return null;
  }
};

// 친구 목록 조회
// export const getFriendsList = async (
//   memberId,
//   lastId = null,
//   pageSize = 30
// ) => {
//   try {
//     if (!memberId) throw new Error("❌ memberId가 필요합니다.");

//     const params = { pageSize };
//     if (lastId) params.lastId = lastId;

//     const response = await apiClient.get(`/${memberId}`, { params });

//     console.log("✅ 친구 목록 조회 결과:", response.data);

//     return response.data;
//   } catch (error) {
//     console.error("[친구 목록 조회 오류]:", error);
//     return [];
//   }
// };
export const getFriendsList = async (
  memberId,
  lastId = null,
  pageSize = 30
) => {
  const client = createClient();

  try {
    if (!memberId) throw new Error("❌ memberId가 필요합니다.");

    const params = { pageSize };
    if (lastId) params.lastId = lastId;

    const response = await client.get(`${FRIEND_BASE_URL}/${memberId}`, {
      params,
    });

    console.log("✅ 친구 목록 조회 결과:", response.data);

    return response.data;
  } catch (error) {
    console.error("[친구 목록 조회 오류]:", error);
    return [];
  }
};

// 차단된 친구 목록 조회
// export const getBlockedFriends = async (lastId = null, pageSize = 30) => {
//   try {
//     const params = { pageSize };
//     if (lastId) params.lastId = lastId;

//     const response = await apiClient.get("/ban", { params });

//     console.log("🚫 차단된 친구 목록 조회 결과:", response.data);

//     // content 배열을 포함하는지 확인하여 반환
//     return response.data;
//   } catch (error) {
//     console.error("[차단된 친구 목록 조회 오류]:", error);
//     return [];
//   }
// };
export const getBlockedFriends = async (lastId = null, pageSize = 30) => {
  const client = createClient();

  try {
    const params = { pageSize };
    if (lastId) params.lastId = lastId;

    const response = await client.get(`${FRIEND_BASE_URL}/ban`, { params });

    console.log("🚫 차단된 친구 목록 조회 결과:", response.data);

    // content 배열을 포함하는지 확인하여 반환
    return response.data;
  } catch (error) {
    console.error("[차단된 친구 목록 조회 오류]:", error);
    return [];
  }
};

// 친구 취소
// export const deleteFriend = async (friendId) => {
//   try {
//     if (!friendId) throw new Error("❌ friendId가 필요합니다.");

//     const response = await apiClient.delete(`/${friendId}`);

//     console.log("🗑 친구 취소 완료:", response);
//     alert("취소되었습니다.");
//     return response.status;
//   } catch (error) {
//     console.error("[친구 취소 오류]:", error);
//     return null;
//   }
// };
export const deleteFriend = async (friendId) => {
  const client = createClient();

  try {
    if (!friendId) throw new Error("❌ friendId가 필요합니다.");

    const response = await client.delete(`${FRIEND_BASE_URL}/${friendId}`);

    console.log("🗑 친구 취소 완료:", response);
    alert("취소되었습니다.");
    return response.status;
  } catch (error) {
    console.error("[친구 취소 오류]:", error);
    return null;
  }
};
