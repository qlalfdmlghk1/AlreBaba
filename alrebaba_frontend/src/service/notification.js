import axios from "axios";
import { createClient } from "./http";

const NOTIFICATION_BASE_URL = `/notifications`;

// 알람 조회
export const getNotifications = async () => {
  const client = createClient();

  try {
    const response = await client.get(`${NOTIFICATION_BASE_URL}`);
    return response.data; // API 응답 데이터 반환
  } catch (error) {
    console.error("알람을 가져오는데 실패하였습니다 :", error);
    throw error;
  }
};

// 알람 삭제
export const deleteNotification = async (notificationId) => {
  const client = createClient();

  try {
    const response = await client.delete(`${NOTIFICATION_BASE_URL}/${notificationId}`);
    return response.data; // API 응답 데이터 반환
  } catch (error) {
    console.error("알람을 삭제하는데 실패하였습니다 :", error);
    throw error;
  }
};
