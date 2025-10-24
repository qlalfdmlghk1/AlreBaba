import React from "react";

function IconLine() {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="100%"
      height="2"
      viewBox="0 0 100 2"
      preserveAspectRatio="none" //  비율 강제 조정 방지
      style={{ display: "block", width: "100%", height: "2px", margin: "0 auto" }}
    >
      <path d="M0 1H860" stroke="#80848E" strokeWidth="0.5" strokeLinecap="round" />
    </svg>
  );
}

export default IconLine;
