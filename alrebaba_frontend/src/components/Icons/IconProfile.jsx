import React from "react";

function IconProfile({ color = "#80848E" }) {
  return (
    <svg
      style={{ paddingRight: "2px" }}
      xmlns="http://www.w3.org/2000/svg"
      width="13"
      height="15"
      viewBox="0 0 15 15"
      fill="none"
    >
      <path
        d="M7.5 5.875C9.29493 5.875 10.75 4.78369 10.75 3.4375C10.75 2.09131 9.29493 1 7.5 1C5.70507 1 4.25 2.09131 4.25 3.4375C4.25 4.78369 5.70507 5.875 7.5 5.875Z"
        stroke={color}
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M14 14.0001H1V12.9167C1 11.6238 1.68482 10.3838 2.90381 9.4696C4.12279 8.55536 5.77609 8.04175 7.5 8.04175C9.22391 8.04175 10.8772 8.55536 12.0962 9.4696C13.3152 10.3838 14 11.6238 14 12.9167V14.0001Z"
        stroke={color}
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

export default IconProfile;
