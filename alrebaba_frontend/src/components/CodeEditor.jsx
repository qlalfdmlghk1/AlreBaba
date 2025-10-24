import React, { useState, useEffect, useRef , forwardRef ,useImperativeHandle } from "react";
import LSEQAllocator from "./LSEQAllocator.js"; // LSEQAllocator 클래스 파일 import
import LWWMap from "./LWWMap.js";
import { Editor } from "@monaco-editor/react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import InnerHeaderLiveCode from "./InnerHeader/InnerHeaderLiveCode";
import { myInfo } from "../service/member.js"
import "./LiveCode.css";
import  LWWRegister  from "./LWWRegister.js";

const CodeEditor = (({ participant, channelId }) => {
  const start = 0;
  const end = [999999999999];
  const maxSendLimit = 950; 
  const lseq = useRef(new LSEQAllocator());
  const lWWMap = useRef(null);
  const [code, setCode] = useState("");
  const prevCode = useRef("");
  const editorRef = useRef(null);
  const BASE_URL = "https://i12a702.p.ssafy.io/api/v1/ws-stomp";
  // const BASE_URL = "http://localhost:8080/api/v1/ws-stomp";
  const [stompClient, setClient] = useState(null);
  const [connection, setConnection] = useState(null);
  const [language, setLanguage] = useState("python");
  const [darkMode, setDarkMode] = useState(true);

  const user = useRef();
  const [accessToken, setAccessToken] = useState(sessionStorage.getItem("accessToken"));
  const history = useRef([]);

  const handleEditorDidMount = (editor, monaco) => {
    editorRef.current = editor;
    const model = editor.getModel();
    model.setEOL(monaco.editor.EndOfLineSequence.LF);

    editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyZ, () => {
      undo();
    });

  };

  const undo = () => {
    if (history.current.length > 0) {
      const lastHistory = history.current.pop();
      const messages = [];

      for (const now of lastHistory) {

        lWWMap.current.merge(now.changeIndex, now.register);
        messages.push({
          key: now.changeIndex,
          register: now.register,
        });

        let jsonString = JSON.stringify(messages);
        let byteSize = new TextEncoder().encode(jsonString).length;

        if (byteSize >= maxSendLimit) {
          const crdtMessage = {
            sessionId: user.current.memberId,
            channelId: channelId,
            content: JSON.stringify(messages),
          };

          sendMessage("crdt" ,crdtMessage);  
          messages.length = 0;
        }

      }

      const crdtMessage = {
        sessionId: user.current.memberId,
        channelId: channelId,
        content: JSON.stringify(messages),
      };
      updateCode();

      sendMessage("crdt" ,crdtMessage);  
      // history.current.length = history.current.length -1;
    }
  }

 const updateCode = () => {

  if (editorRef.current) {
    const editor = editorRef.current;
    const model = editor.getModel();

    if (model) {
        const newText = lWWMap.current.text;
        const oldText = model.getValue();

        if (oldText !== newText) {
          console.log("updateCode");
          editor.executeEdits("", [
            {
              range: model.getFullModelRange(),
              text: newText,
            },
          ]);
        }

      // setCode(lWWMap.current.text);

    }
  }
 }

  const handleCodeChange = (value, event) => {
    
    if (value.length >= 6000) {
      alert("6000이상은 입력이 불가능 합니다!")
      // setCode(prevCode.current);
      editorRef.current?.getModel()?.setValue(prevCode.current);
      return;
    }
    if(prevCode.current === value){
      return;
    }
 
    prevCode.current = value; 

    const text = lWWMap.current.text;
    const [indexes, changeLength] = indexOfChange(text, value);
    const messages = [];
    const tempHistory = [];


    indexes.forEach((index) => {

      const values = lWWMap.current.value;
      const left = values[index][0];
      let right = values[index + 1][0];
      let change = value[index];
      let before = values[index+1][1];
      let changeIndex = right;
      if(changeLength === -1){
        change = null;
      }
      if (changeLength >= 1) {
        if(right !== end && (right === "" || right === undefined)){
          changeIndex = right;
        }
        else{
          changeIndex = lseq.current.alloc(JSON.parse(left), JSON.parse(right), changeLength);    
        }
      }
      else {
        changeIndex = right;
      }  
  

      lWWMap.current.set(changeIndex, change);
      const beforeRegister = lWWMap.current.get(changeIndex, change);

        const nextRegister = new LWWRegister([beforeRegister.state[0], -1*(beforeRegister.state[1] +1), before]);
        tempHistory.push({ changeIndex: changeIndex, register: nextRegister });

      messages.push({
        key: changeIndex,
        register: beforeRegister,
      });
  
    
      let jsonString = JSON.stringify(messages);
      let byteSize = new TextEncoder().encode(jsonString).length;

      if (byteSize >= maxSendLimit) {
        const crdtMessage = {
          sessionId: user.current.memberId,
          channelId: channelId,
          content: JSON.stringify(messages),
        };
        sendMessage("crdt" ,crdtMessage);  
        messages.length = 0;
      }

    });
    
    if(messages.length > 0){
      const crdtMessage = {
        sessionId: user.current.memberId,
        channelId: channelId,
        content: JSON.stringify(messages),
      };
      history.current.push(tempHistory)
      updateCode();
      sendMessage("crdt" ,crdtMessage); 
    }


  };

  const indexOfChange = (before, after) => {
    const indexes = [];
 
    const maxLength = Math.max(before.length, after.length);
    let changeLength = after.length - before.length;

    let firstBefore = null;

    for (let i = 0; i < maxLength; i++) {
      if (before[i] !== after[i]) {
        if(firstBefore === null){
          firstBefore = before[i];
        }
        else if(firstBefore === after[i]){
          break;
        }
        indexes.push(i);
        if (Math.abs(changeLength) <= 1) {
          break;
        }
      }
    }


    return [indexes, changeLength];
  };


  const sendMessage = (dest, crdtMessage) => {
    if (stompClient && crdtMessage!== null) {
      // console.log("crdtMessage ",JSON.stringify(crdtMessage))
    stompClient.publish({
      access: accessToken,
      destination: `/app/${dest}`, // Backend's @MessageMapping endpoint "/app/send"
      body: JSON.stringify(crdtMessage),
    });
    }
  }


  const sendAll = (id, dest) => {
    sendWithBuffer( id , dest, lWWMap.current.allMessage);
  };

  const sendWithBuffer = (id, dest, messages) => {
    const jsonBuffer = [];
    let byteSize = 0;
    const textEncoder = new TextEncoder();

      messages.forEach((message) => {
        const temp = JSON.stringify(message);
        const tempSize = textEncoder.encode(temp).length;

        if (byteSize + tempSize > maxSendLimit) {
          const crdtMessage = {
            sessionId: id,
            channelId: channelId,
            content: JSON.stringify(jsonBuffer),
          };
          
          sendMessage(dest, crdtMessage);

          jsonBuffer.length = 0;
          byteSize = 0;
        }
    
        jsonBuffer.push(message);
        byteSize += tempSize;
      });

      if (jsonBuffer.length > 0) {
        const crdtMessage = {
          sessionId: id,
          channelId: channelId,
          content: JSON.stringify(jsonBuffer),
        };
   
        sendMessage(dest, crdtMessage);
      }
    
  };

  useEffect(() => {
    async function fetchUserInfoAndInitClient() {
      try {
        const response = await myInfo();
        user.current = response.data;
        lWWMap.current = new LWWMap(user.current.memberId);
        if (response.success) {
          // setUser(response.data);

          const stompClient = new Client({
            webSocketFactory: () => new SockJS(BASE_URL),
            connectHeaders: {
              access: accessToken,
              channelId : channelId,
            },
            reconnectDelay: 5000, 
            onConnect: () => {
              console.log("socket connected!")
              // 채널 구독
              stompClient.subscribe(`/topic/code/${channelId}`, (msg) => {
                handleDataReceived(msg);
              });

              // stompClient.subscribe(`/user/${response.data.username}/topic/event`, (msg) => {
              //   handelEventReceived(msg);
              // });
              
              // stompClient.subscribe(`/user/${response.data.username}/topic/snapshot/${channelId}`, (msg) => {
              //   // console.log(" get  snapshot",msg);
              //   handleDataReceived(msg);
              // })
              setConnection(true);
            },
            onStompError: (frame) => {
              console.error("Broker reported error: " + frame.headers["message"]);
              console.error("Additional details: " + frame.body);
              setConnection(null);
            },
          });
          setClient(stompClient);
          stompClient.activate(); 
          
          // Clean-up
          return () => {
            stompClient.deactivate(); // 컴포넌트 언마운트 시 STOMP 클라이언트 비활성화
            setConnection(null);
          };
        } else {
          console.error("사용자 정보를 불러오는데 실패했습니다.");
        
        }
      } catch (error) {
        console.error("사용자 정보를 가져오는 중 오류 발생:", error);
        setConnection(null);
      }
    }
  
    fetchUserInfoAndInitClient();

  }, [channelId]); // channelId가 변경될 때마다 재실행
  

  const handleDataReceived = (payload) => {
    const decodedMessage = new TextDecoder().decode(payload._binaryBody.buffer);
    const msgs = JSON.parse(decodedMessage);
    const messages = JSON.parse(msgs.content);
    console.log(messages);
    if (messages !== '' && messages.length !== 0) {
      if(messages.length === 1 && messages[0].key === -1){
        setLanguage(messages[0].register);
        return 
      }

      messages.forEach((msg) => {
        const state = msg.register.state;
        // if(state[0] !== user.current.memberId){
          lWWMap.current.merge(msg.key, state);
          updateCode()
        // }
      })         
    }

  
  };

  const handelEventReceived = (payload) => {
    const decodedMessage = new TextDecoder().decode(payload._binaryBody.buffer);
    const msgs = JSON.parse(decodedMessage);
    const targetEventId = msgs.targetEventId;

    if (targetEventId !== undefined) {
      setTimeout(() => {
        sendAll(targetEventId, "snapshot");
      }, 3000);
    }

  };

  const handleModeChange = () => {
    setDarkMode(!darkMode);
  };

  const handleLanguageChange = (value) => {
    const message = {
      sessionId: user.current.memberId,
      channelId: channelId,
      content: JSON.stringify([{ key: -1, register: value}])
    };
    sendMessage("crdt", message);
    setLanguage(value);
  };

  return (
    <>
     <InnerHeaderLiveCode
        darkMode={darkMode}
        language={language}
        handleModeChange={handleModeChange}
        handleLanguageChange={handleLanguageChange}
      />
      <div className="live-code-container">
        {connection ? (
          <Editor
            height="calc(100vh - 154px)"
            language={language}
            // value={code}
            onChange={handleCodeChange}
            onMount={handleEditorDidMount}
            theme={darkMode ? "vs-dark" : "vs-light"}
            options={{
              fontSize: 18,
              minimap: { enabled: false },
              selectOnLineNumbers: true,
              scrollbar: {
                vertical: "auto",
                horizontal: "auto",
              },
            }}
          />) : <div>Loding</div>}
  

      </div>
      </>
  );
});

export default CodeEditor;