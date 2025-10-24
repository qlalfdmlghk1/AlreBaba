import React, { useState } from "react";
import { signup } from "/src/service/member";
import ButtonKakao from "../Buttons/ButtonKakao";
import ButtonGitHub from "../Buttons/ButtonGitHub";
import "./ModalBase.css";
import "./ModalRegister.css";

function ModalRegister({ onClose, onSingin }) {
  const handleContainerClick = (e) => {
    // modal 내부를 클릭했을 경우엔 onClose 실행 안되게게
    e.stopPropagation();
  };

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [passwordCheck, setPasswordCheck] = useState("");
  const [nickname, setNickname] = useState("");

  const handleSubmit = async (e) => {
    // 폼 제출 시 새로고침 방지
    e.preventDefault();

    // 입력 검증
    if (!nickname.trim()) {
      alert("닉네임을 입력해 주세요.");
      return;
    }
    if (nickname.trim().length < 2) {
      alert("닉네임은 최소 2글자 이상입니다.");
      return;
    }
    if (!username.trim()) {
      alert("이메일을 입력해 주세요.");
      return;
    }

    const hasLetter = /[a-zA-Z]/.test(password); // 영문자 포함 여부
    const hasNumber = /\d/.test(password); // 숫자 포함 여부
    const hasSpecialChar = /[\W_]/.test(password); // 특수문자 포함 여부
    if (!password.trim()) {
      alert("비밀번호를 입력해 주세요.");
      return;
    }
    if (password.trim().length < 8) {
      alert("비밀번호는 8글자 이상이어야 합니다.");
      return;
    }
    if (!(hasLetter && hasNumber && hasSpecialChar)) {
      alert("비밀번호는 영문자, 숫자, 특수문자를 포함해야 합니다.");
      return;
    }
    if (password.trim() != passwordCheck.trim()) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    // API 요청
    const result = await signup(username, password, nickname);

    console.log(result);

    if (result.success) {
      // 회원가입 성공 처리(수정 필요)
      alert("회원가입이 완료되었습니다!");
      // 로그인 모달 띄우기
      onSingin();
    } else {
      // 에러 처리(조건 분기 세분화? 해야됨)
      console.error(result);
    }
  };

  return (
    <div className="container" onMouseDown={onClose}>
      <div className="modal" onMouseDown={handleContainerClick}>
        <h1 className="title">ALREBABA</h1>
        <form className="login-form" onSubmit={handleSubmit}>
          <input
            type="nickname"
            placeholder="Nickname"
            className="input-field"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
          />
          <input
            type="email"
            placeholder="Email"
            className="input-field"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            className="input-field"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <input
            type="password"
            placeholder="PasswordCheck"
            className="input-field"
            value={passwordCheck}
            onChange={(e) => setPasswordCheck(e.target.value)}
          />
          <button type="submit" className="register-button">
            회원가입
          </button>
        </form>
        <div className="divider-container">
          <div className="line"></div>
          <span className="divider-text">OR</span>
          <div className="line"></div>
        </div>
        <div className="social-login">
          <ButtonKakao />
          <ButtonGitHub />
        </div>
        <div className="signup">
          이미 계정이 있나요?{" "}
          <a className="signup-link" onClick={onSingin}>
            Sign In
          </a>
        </div>
      </div>
    </div>
  );
}

export default ModalRegister;
