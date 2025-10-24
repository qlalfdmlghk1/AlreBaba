import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import MarkdownToggleButton from "../Buttons/MarkdownToggleButton";
import { getStudyRole } from "../../service/studyParticipant";
import "./InnerHeaderMarkdown.css";

function InnerHeaderMarkdown({ isEditing, toggleEditing }) {
  const { studyId } = useParams();
  const [role, setRole] = useState("");

  useEffect(() => {
    const fetchStudyRole = async () => {
      try {
        const response = await getStudyRole(studyId);
        setRole(response.data.role); // role 상태 업데이트
      } catch (error) {
        console.log("스터디 역할 불러오기 실패:", error);
      }
    };

    fetchStudyRole();
  }, [studyId]);

  return (
    <div className="inner-header">
      <div className="left"></div>
      {role === "OWNER" ? (
        <MarkdownToggleButton
          className="MarkdownToggleButton"
          isEditing={isEditing}
          onToggle={toggleEditing}
        />
      ) : (
        <></>
      )}
    </div>
  );
}

export default InnerHeaderMarkdown;
