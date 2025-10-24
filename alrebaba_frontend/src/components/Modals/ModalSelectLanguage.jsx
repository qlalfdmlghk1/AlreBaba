import React, { useEffect, useState } from "react";
// 언어 아이콘 import
import IconC from "../Icons/IconC";
import IconCPP from "../Icons/IconCPP";
import IconCS from "../Icons/IconCS";
import IconDart from "../Icons/IconDart";
import IconGo from "../Icons/IconGo";
import IconJava from "../Icons/IconJava";
import IconJavaScript from "../Icons/IconJavaScript";
import IconKotlin from "../Icons/IconKotlin";
import IconPHP from "../Icons/IconPHP";
import IconPython from "../Icons/IconPython";
import IconR from "../Icons/IconR";
import IconRust from "../Icons/IconRust";
import IconSQL from "../Icons/IconSQL";
import IconSwift from "../Icons/IconSwift";
import IconTypeScript from "../Icons/IconTypeScript";
import "./ModalBase.css";
import "./ModalSelectDomain.css";
import IconRuby from "../Icons/IconRuby";

function ModalSelectLanguage({ onClose, onSave, selectedLanguages }) {
  const [languages, setLanguages] = useState([]);

  useEffect(() => {
    setLanguages(selectedLanguages);
  }, [selectedLanguages]);

  // 언어 클릭 시 선택 상태 토글
  const toggleLanguage = (languageId) => {
    setLanguages((prev) => {
      const safePrev = prev || []; // prev가 null이면 빈 배열 사용
      return safePrev.includes(languageId)
        ? safePrev.filter((id) => id !== languageId)
        : safePrev.length < 5
        ? [...safePrev, languageId]
        : safePrev;
    });
  };

  // 언어 목록 (id 기준으로 설정)
  const languageList = [
    { id: "C", name: "C", icon: <IconC width={60} height={60} /> },
    { id: "C_PlusPlus", name: "C++", icon: <IconCPP width={60} height={60} /> },
    { id: "C_Sharp", name: "C#", icon: <IconCS width={60} height={60} /> },
    // { id: "Dart", name: "Dart", icon: <IconDart width={60} height={60} /> },
    { id: "Go", name: "Go", icon: <IconGo width={60} height={60} /> },
    { id: "Java", name: "Java", icon: <IconJava width={60} height={60} /> },
    {
      id: "JavaScript",
      name: "JavaScript",
      icon: <IconJavaScript width={60} height={60} />,
    },
    {
      id: "kotlin",
      name: "Kotlin",
      icon: <IconKotlin width={60} height={60} />,
    },
    { id: "PHP", name: "PHP", icon: <IconPHP width={60} height={60} /> },
    {
      id: "Python",
      name: "Python",
      icon: <IconPython width={60} height={60} />,
    },
    // { id: "R", name: "R", icon: <IconR width={60} height={60} /> },
    { id: "Rust", name: "Rust", icon: <IconRust width={60} height={60} /> },
    { id: "Ruby", name: "Ruby", icon: <IconRuby width={60} height={60} /> },
    { id: "SQL", name: "SQL", icon: <IconSQL width={60} height={60} /> },
    { id: "Swift", name: "Swift", icon: <IconSwift width={60} height={60} /> },
    {
      id: "TypeScript",
      name: "TypeScript",
      icon: <IconTypeScript width={60} height={60} />,
    },
  ];

  return (
    <div className="container">
      <div className="modal">
        <div className="header">
          주력 언어를 선택해주세요
          <br />
          <div className="explane">최대 5개까지 선택 가능합니다.</div>
        </div>
        <div className="domains">
          {languageList.map((language) => (
            <div
              key={language.id}
              className={`domain ${
                languages.includes(language.id) ? "selected" : ""
              }`}
              onClick={() => toggleLanguage(language.id)}
            >
              {language.name}
              <div className="icon">{language.icon}</div>
            </div>
          ))}
        </div>
        <div className="button-container">
          <button className="cancel" onClick={onClose}>
            취소
          </button>
          <button className="create" onClick={() => onSave(languages)}>
            확인
          </button>
        </div>
      </div>
    </div>
  );
}

export default ModalSelectLanguage;
