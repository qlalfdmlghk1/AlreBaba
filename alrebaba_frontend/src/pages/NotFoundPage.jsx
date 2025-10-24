import React from "react";
import "./NotFoundPage.css";

const NotFoundPage = () => {
  const goBack = () => {
    window.history.back();
  };

  return (
    <div className="not-found-container">
      <img
        src="https://i.imgur.com/qIufhof.png"
        alt="길을 잃은 캐릭터"
        className="not-found-image"
      />

      <h1 className="not-found-title">404</h1>
      <p className="not-found-message">
        죄송합니다, 페이지를 찾을 수 없습니다.
      </p>
      <p className="not-found-description">
        존재하지 않는 주소를 입력하셨거나
        <br />
        요청하신 페이지가 이동되었거나 삭제되어 찾을 수 없습니다.
      </p>

      <div className="not-found-button-container">
        <button onClick={goBack} className="not-found-button">
          이전 페이지로 돌아가기
        </button>
      </div>
    </div>
  );
};

export default NotFoundPage;
