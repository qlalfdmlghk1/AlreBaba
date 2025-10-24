import { useState } from "react";

import IconDarkMode from "../Icons/IconDarkMode";
import IconLightMode from "../Icons/IconLightMode";
import IconCPP from "../Icons/IconCPP";
import IconJava from "../Icons/IconJava";
import IconPython from "../Icons/IconPython";
import IconJavaScript from "../Icons/IconJavaScript";

import "./InnerHeaderLiveCode.css";

function InnerHeaderLiveCode({
  darkMode,
  language,
  handleModeChange,
  handleLanguageChange,
}) {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const languages = [
    { name: "c++", icon: <IconCPP width={20} height={20} /> },
    { name: "java", icon: <IconJava width={20} height={20} /> },
    { name: "python", icon: <IconPython width={20} height={20} /> },
    { name: "javascript", icon: <IconJavaScript width={20} height={20} /> },
  ];

  return (
    <>
      <div className="inner-header">
        <div className="left">
          <div className="buttons">
            {/* <span className="button red"></span>
            <span className="button yellow"></span>
            <span className="button green"></span> */}
          </div>
        </div>
        <div
          className="language-container"
          onClick={() => setIsModalOpen(!isModalOpen)}
          tabIndex={0}
          onBlur={() => setIsModalOpen(false)}
        >
          {languages.find((lang) => lang.name === language)?.icon}
          {language.toUpperCase()}
        </div>
        {/* 모달 */}
        <div className={`language-modal ${isModalOpen ? "open" : ""}`}>
          {languages.map((lang) => (
            <div
              key={lang.name}
              className="language-option"
              onMouseDown={() => {
                handleLanguageChange(lang.name);
                setIsModalOpen(false);
              }}
            >
              {lang.icon}
              {lang.name.toUpperCase()}
            </div>
          ))}
        </div>

        <div className="darkmode-icon-container" onClick={handleModeChange}>
          {darkMode ? <IconDarkMode /> : <IconLightMode />}
        </div>
      </div>
    </>
  );
}

export default InnerHeaderLiveCode;
