import { createClient } from "./http";
import axios from "axios";

const LANGUAGE_VERSIONS = {
  javascript: "18.15.0",
  python: "3.10.0",
  java: "15.0.2",
  csharp: "6.12.0",
  c: "10.2.0",
  "c++": "10.2.0",
  go: "1.16.2",
  ruby: "3.0.1",
  rust: "1.68.2",
};

// 외부 API를 이용한 코딩 테스트 문제 실행
export const executeCode = async (language, sourceCode) => {
  if (language === "node.js") {
    language = "javascript";
  } else if (language === "C#") {
    language = "csharp";
  }
  const response = await axios.post("https://emkc.org/api/v2/piston/execute", {
    language: language.toLowerCase(),
    version: LANGUAGE_VERSIONS[language.toLowerCase()],
    files: [
      {
        content: sourceCode,
      },
    ],
  });
  return response.data;
};

const CODE_BASE_URL = "code";

// 코드 생성 API
export async function postCTCode(channelId, data) {
  try {
    const client = createClient();
    const response = await client.post(`${CODE_BASE_URL}/${channelId}`, data);
    return { success: true, data: response.data };
  } catch (error) {
    console.error(
      "코드 보내기 실패:",
      error.response ? error.response.data : error
    );
    return { success: false, error: error.response?.data || error.message };
  }
}

// code 조회
export async function getCode(data) {
  try {
    const client = createClient();

    const response = await client.get(`${CODE_BASE_URL}`, {
      params: data,
    });

    return { success: true, data: response.data };
  } catch (error) {
    console.error(
      "코드 정보 조회 실패:",
      error.response ? error.response.data : error
    );
    return { success: false, error: error.response?.data || error.message };
  }
}

// 코드 가져오기
export async function getCodeDetail(codeId) {
  try {
    const client = createClient();

    const response = await client.get(`${CODE_BASE_URL}/details/${codeId}`);

    return { success: true, data: response.data };
  } catch (error) {
    console.error(
      "코드 보내기 실패:",
      error.response ? error.response.data : error
    );
    return { success: false, error: error.response?.data || error.message };
  }
}

// 코드 수정 API
export async function patchCTCode(data) {
  try {
    const client = createClient();

    const response = await client.patch(`${CODE_BASE_URL}`, data);
    return { success: true, data: response.data };
  } catch (error) {
    console.error(
      "코드 수정 실패:",
      error.response ? error.response.data : error
    );
    return { success: false, error: error.response?.data || error.message };
  }
}
