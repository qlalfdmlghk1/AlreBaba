import React from "react";
import "./TitleBarContainer.css";

function TitleBarContainer({ titleIcon, name, children }) {
  return (
    <div className="title-bar-container">
      <p className="title-icon">{titleIcon}</p>
      <p className="name">{name}</p>
      {children}
    </div>
  );
}

export default TitleBarContainer;
