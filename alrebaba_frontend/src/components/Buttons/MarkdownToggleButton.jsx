import React from "react";
import "./MarkdownToggleButton.css";
import IconEdit from "../Icons/IconEdit";
import IconSave from "../Icons/IconSave";

function MarkdownToggleButton({ isEditing, onToggle }) {
  return (
    <div className="markdown-save-edit" onClick={onToggle}>
      {isEditing ? <IconSave /> : <IconEdit />}
    </div>
  );
}

export default MarkdownToggleButton;
