import axios from "axios";
import { createClient } from "./http";

const CODING_TEST_BASE_URL = "coding-tests";

// 코딩 테스트 문제 생성 (새로운 API)
export async function createCT(data) {
  try {
    const client = createClient();
    const response = await client.post(`${CODING_TEST_BASE_URL}`, data);
    return { success: true, data: response.data };
  } catch (error) {
    console.error(
      "코딩 테스트 생성 실패:",
      error.response ? error.response.data : error
    );
    return { success: false, error: error.response?.data || error.message };
  }
}

// 코딩 테스트 문제 조회 API
export async function getCTProblems(testId) {
  try {
    const client = createClient();

    const response = await client.get(
      `${CODING_TEST_BASE_URL}/${testId}/problems`
    );
    return { success: true, data: response.data };
  } catch (error) {
    console.error(
      "코딩 테스트 문제 조회 실패:",
      error.response ? error.response.data : error
    );
    return { success: false, error: error.response?.data || error.message };
  }
}

export async function getCTInfo(data) {
  try {
    const client = createClient();

    const response = await client.get(`${CODING_TEST_BASE_URL}`, {
      params: data,
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error(
      "코딩 테스트 정보 조회 실패:",
      error.response ? error.response.data : error
    );
    return { success: false, error: error.response?.data || error.message };
  }
}
