import React from "react";

import "./EventTag.css";

// Hex 색상값을 rgba로 변환하는 함수
const hexToRgba = (hex, alpha) => {
  let r = 0,
    g = 0,
    b = 0;

  // 3자리와 6자리 hex 처리
  if (hex.length === 4) {
    r = parseInt(hex[1] + hex[1], 16);
    g = parseInt(hex[2] + hex[2], 16);
    b = parseInt(hex[3] + hex[3], 16);
  } else if (hex.length === 7) {
    r = parseInt(hex[1] + hex[2], 16);
    g = parseInt(hex[3] + hex[4], 16);
    b = parseInt(hex[5] + hex[6], 16);
  }

  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

function EventTag({ date, events, onClick }) {
  const handleClick = (event, eventId) => {
    event.stopPropagation();
    onClick(eventId);
  };

  return (
    <div
      className="event-tag-container"
      style={{
        position: "relative",
        height: "90%",
        marginTop: "15px",
        overflowY: "auto",
        textAlign: "left",
      }}
    >
      {events.map((event, index) => {
        // event.colorCode에 투명도 적용한 배경색 계산
        const backgroundColor = hexToRgba(event.colorCode, 0.1);

        return (
          <div
            key={event.eventId}
            style={{
              display: "flex",
              justifyContent: "center",
              border: `2px ${event.colorCode} solid`,
              position: "absolute",
              borderRadius: "4px",
              top: `${index * 35 + 15}px`,
              width: "100%",
              padding: "2px",
              backgroundColor: backgroundColor, // 배경색 설정
            }}
            onClick={(e) => handleClick(e, event.eventId)}
          >
            <div
              style={{
                fontWeight: "bold",
                whiteSpace: "nowrap",
                overflow: "hidden",
                textOverflow: "ellipsis",
                width: "100%",
                padding: "2px 0",
                color: "black",
              }}
            >
              {event.startTime + " " + event.eventName}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default EventTag;
