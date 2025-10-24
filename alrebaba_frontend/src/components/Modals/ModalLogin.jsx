import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ButtonKakao from "../Buttons/ButtonKakao";
import ButtonGitHub from "../Buttons/ButtonGitHub";
import { login } from "/src/service/member";
import "./ModalBase.css";
import "./ModalLogin.css";

function ModalLogin({ onClose, onSingup }) {
  const navigate = useNavigate();
  const handleContainerClick = (e) => {
    // modal 내부를 클릭했을 경우엔 onClose 실행 안되게게
    e.stopPropagation();
  };

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    // 입력 검증
    if (!email.trim()) {
      alert("이메일을 입력해 주세요.");
      e.preventDefault(); // 폼 제출 시 새로고침 방지
      return;
    }
    if (!password.trim()) {
      alert("비밀번호를 입력해 주세요.");
      e.preventDefault(); // 폼 제출 시 새로고침 방지
      return;
    }

    e.preventDefault();

    // API 요청
    const { success, data, response } = await login(email, password);

    if (success) {
      sessionStorage.setItem("userData.createdAt", data.createdAt);
      sessionStorage.setItem("userData.interests", data.interests);
      sessionStorage.setItem("userData.isAlarmOn", data.isAlarmOn);
      sessionStorage.setItem("userData.languages", data.languages);
      sessionStorage.setItem("userData.memberId", data.memberId);
      sessionStorage.setItem("userData.nickname", data.nickname);
      sessionStorage.setItem("userData.profileImage", data.profileImage);
      sessionStorage.setItem("userData.role", data.role);
      sessionStorage.setItem("userData.status", data.status);
      sessionStorage.setItem("userData.username", data.username);
      sessionStorage.setItem("userData.uniqueId", data.uniqueId);
      sessionStorage.setItem(
        "userData.interests",
        JSON.stringify(data.interests ?? [])
      );
      sessionStorage.setItem(
        "userData.languages",
        JSON.stringify(data.languages ?? [])
      );

      // 회원가입 성공 처리
      alert("로그인 성공!!");
      onClose();
      navigate("/friend"); // FriendPage.jsx로 이동
    } else {
      // 에러 처리(조건 분기 세분화? 해야됨)
      console.error(response);
    }
  };

  return (
    <div className="container" onMouseDown={onClose}>
      <div className="modal" onMouseDown={handleContainerClick}>
        <h1 className="title">ALREBABA</h1>
        <form className="login-form" onSubmit={handleSubmit}>
          <input
            type="email"
            placeholder="Email"
            className="input-field"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            className="input-field"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="submit" className="login-button">
            로그인
          </button>
        </form>
        {/* <a href="#" className="forgot-password">
          비밀번호를 잊으셨나요?
        </a> */}
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
          등록하지 않으셨나요?{" "}
          <a className="signup-link" onClick={onSingup}>
            Sign Up
          </a>
        </div>
      </div>
    </div>
  );
}

export default ModalLogin;
