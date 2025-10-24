import { io } from "socket.io-client";

BASE_URL = "wss://i12a702.p.ssafy.io:8080/api/v1/ws-stomp";

const socket = io("http://localhost:8080");

export const sendMesage = async () => {
  const response = await client.post(`${BASE_URL}/${studyId}/channels`);
};
