import React from "react";
import "./StatusMenu.css";
import IconOnline from "./Icons/IconOnline";
import IconAway from "./Icons/IconAway";
import IconDoNotDisturb from "./Icons/IconDoNotDisturb";
import IconOffline from "./Icons/IconOffline";
import IconSelectStatus from "./Icons/IconSelectStatus";

function StatusMenu({ onSelect }) {
  return (
    <div className="status-menu">
      <div className="status-menu-item-container" onClick={() => onSelect("ONLINE")}>
        <div className="icon-container">
          <IconOnline />
        </div>
        <span className="status-icon-check">
          <p>온라인</p>
          <span className="status-check">
            <IconSelectStatus />
          </span>
        </span>
      </div>
      <hr className="status-menu-hr" />
      <div className="status-menu-item-container" onClick={() => onSelect("ON_ANOTHER_BUSINESS")}>
        <div className="icon-container">
          <IconAway />
        </div>
        <span className="status-icon-check">
          <p>자리 비움</p>
          <span className="status-check">
            <IconSelectStatus />
          </span>
        </span>
      </div>
      <hr className="status-menu-hr" />
      <div className="status-menu-item-container" onClick={() => onSelect("NO_INTERFERENCE")}>
        <div className="icon-container">
          <IconDoNotDisturb />
        </div>
        <span className="status-icon-check">
          <p>방해금지</p>
          <span className="status-check">
            <IconSelectStatus />
          </span>
        </span>
      </div>
      <hr className="status-menu-hr" />
      <div className="status-menu-item-container" onClick={() => onSelect("OFF_LINE")}>
        <div className="icon-container">
          <IconOffline />
        </div>
        <span className="status-icon-check">
          <p>오프라인</p>
          <span className="status-check">
            <IconSelectStatus />
          </span>
        </span>
      </div>
    </div>
  );
}

export default StatusMenu;
