import { createClient } from "./http";

const CHANNEL_BASE_URL = "studies";

// 채널 생성
export const createChannel = async (studyId, chanelInfo, channelName) => {
  const client = createClient();

  const response = await client.post(
    `${CHANNEL_BASE_URL}/${studyId}/channels`,
    {
      channelName: channelName,
      channelType: chanelInfo.channelType,
    }
  );
  return response.status;
};

// 채널 목록 조회
export const getStudyChannels = async (studyId) => {
  const client = createClient();

  const response = await client.get(`${CHANNEL_BASE_URL}/${studyId}/channels`);
  return response.data;
};

// 채널 삭제
export const deleteChannel = async (studyId, channelId) => {
  const client = createClient();

  // console.log(`${CHANNEL_BASE_URL}/${studyId}/channels/${channelId}`);

  const response = await client.delete(
    `${CHANNEL_BASE_URL}/${studyId}/channels/${channelId}`
  );
  return response.data;
};

// 채널 이름 변경
export const updateChannelName = async (studyId, channelId, channelName) => {
  const client = createClient();

  console.log(`${CHANNEL_BASE_URL}/${studyId}/channels/${channelId}`);

  const response = await client.patch(
    `${CHANNEL_BASE_URL}/${studyId}/channels/${channelId}`,
    {
      channelName,
    }
  );
  return response.status;
};
