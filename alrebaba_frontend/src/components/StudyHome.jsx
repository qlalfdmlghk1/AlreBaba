import MDEditor from "@uiw/react-md-editor";
import { Editor } from "@monaco-editor/react";
import React, { useEffect, useState } from "react";
import "./StudyHome.css";
import InnerHeaderMarkdown from "./InnerHeader/InnerHeaderMarkdown";
import InnerFooter from "./InnerFooter";
import { updateStudyDescription, studyDetail } from "../service/study.js";

function StudyHome({ studyId }) {
  const [mdinfo, setMD] = useState(""); // 작성 중인 마크다운 데이터
  const [mkdStr, setMkdStr] = useState("");
  const [isEditing, setIsEditing] = useState(false); // 현재 상태 (편집 중인지 여부)

  // studyId가 변경될 때 마크다운 내용 업데이트
  useEffect(() => {
    const fetchStudy = async () => {
      if (!studyId) return;

      const result = await studyDetail(studyId);
      setIsEditing(false);

      if (result.data.description !== null) {
        setMkdStr(result.data.description.replace(/\\n/g, "\n"));
      } else {
        setMkdStr(
          `<h1> ${result.data.studyName}스터디에 오신 것을 환영합니다!!\n\n 스터디 설명을 작성해주세요! </h1>`
        );
      }
    };
    fetchStudy();
  }, [studyId]); // studyId가 변경될 때 실행됨

  // 버튼 클릭 시 동작
  const toggleEditing = async () => {
    if (isEditing) {
      // "수정" 상태에서 "저장" 상태로 전환

      // ✅ API 호출하여 서버에 업데이트
      const result = await updateStudyDescription(studyId, mdinfo);
      if (result.success) {
        setMkdStr(result.content.replace(/\\n/g, "\n"));
      }
    } else {
      // "저장" 상태에서 "수정" 상태로 전환
      setMD(mkdStr);
    }
    setIsEditing(!isEditing); // 상태 토글
  };

  return (
    <div className="study-home">
      <InnerHeaderMarkdown
        isEditing={isEditing}
        toggleEditing={toggleEditing}
      />
      {/* 마크다운 뷰어 */}
      {!isEditing && (
        <div className="markdownDiv">
          <MDEditor.Markdown className="markdownContent" source={mkdStr} />
        </div>
      )}

      {/* 마크다운 에디터 */}
      {isEditing && (
        <Editor
          height="calc(100vh - 154px)"
          defaultLanguage="markdown"
          value={mdinfo}
          onChange={setMD}
          options={{
            fontSize: 18,
            minimap: { enabled: false },
            selectOnLineNumbers: true,
            scrollbar: {
              vertical: "auto",
              horizontal: "auto",
            },
          }}
        />
      )}
      <InnerFooter />
    </div>
  );
}

export default StudyHome;
