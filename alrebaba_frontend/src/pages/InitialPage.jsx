import React, { useState, useEffect } from "react";

import banner from "../assets/images/initial-banner.png";
import ModalLogin from "../components/Modals/ModalLogin";
import ModalRegister from "../components/Modals/ModalRegister";
import { useNavigate } from "react-router-dom";
import { initMember, myInfo, initSSE } from "../service/member";
import {getAccessTokenByRefreshToken} from "../service/http";
import "./InitialPage.css";

function Initial() {
  // 모달 상태 관리: 'login', 'register', 'none'
  const [modalState, setModalState] = useState("none");

  const openLoginModal = () => setModalState("login");
  const openRegisterModal = () => setModalState("register");
  const closeModal = () => setModalState("none");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
        try {
          await getAccessTokenByRefreshToken(); 
          const { success, data, response } = await initMember(); 
          if (success) {
            navigate("/friend");
          }
        } catch (error) {
          console.error(error);
        }
      }
    
    fetchData();
  }, []);
  


  return (
    <>
      {/* 로그인 모달 */}
      {modalState === "login" && (
        <ModalLogin onClose={closeModal} onSingup={openRegisterModal} />
      )}
      {/* 회원가입 모달 */}
      {modalState === "register" && (
        <ModalRegister onClose={closeModal} onSingin={openLoginModal} />
      )}

      <div className="initial-container">
        <div className="content">
          <h1>ALGORITHM REVIEW BAROBARO</h1>
          <h2>
            Collaborate seamlessly on algorithm code reviews with our real-time
            editing platform. Enhance teamwork, streamline feedback, and
            optimize code quality—all in one intuitive space.
          </h2>
          <button onClick={openLoginModal}>Sign in</button>
        </div>
        <div className="banner">
          <img src={banner} alt="code" />
        </div>
      </div>
    </>
  );
}

export default Initial;
