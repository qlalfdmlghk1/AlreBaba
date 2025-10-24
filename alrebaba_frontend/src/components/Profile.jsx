import React, { useEffect, useState } from "react";
import Badge from "./Badge";
import IconAdd from "./Icons/IconAdd";
import {
  myInfo,
  updateInterests,
  updateLanguages,
  updateNickname,
  updateProfileImage,
} from "../service/member";
import ModalSelectDomain from "./Modals/ModalSelectDomain";
import ModalSelectLanguage from "./Modals/ModalSelectLanguage";
import IconBlock from "./Icons/IconBlock";
import "./Profile.css";
import defaultProfileImage from "../assets/images/basicImage.jpg";

function Profile({ profileImage, setProfileImage, nickname, setNickname }) {
  const memberId = sessionStorage.getItem("userData.memberId");
  const initialNickname = nickname;
  const [prevProfileImage, setPrevProfileImage] = useState(profileImage); // 기존 프로필 이미지 저장

  const [isEditing, setIsEditing] = useState(false);
  const [isDomainModalOpen, setIsDomainModalOpen] = useState(false); // 관심 도메인 모달 상태 추가
  const [isLanguageModalOpen, setIsLanguageModalOpen] = useState(false); // 관심 도메인 모달 상태 추가
  const [selectedDomains, setSelectedDomains] = useState([]); // 관심 도메인 상태 추가
  const [selectedLanguages, setSelectedLanguages] = useState([]); // 관심 도메인 상태 추가
  const [nicknameError, setNicknameError] = useState("");

  // 관심 도메인 모달 열기
  const handleOpenDomainModal = () => {
    setIsDomainModalOpen(true);
  };

  // 관심 도메인 업데이트 (API 호출 포함)
  const handleSaveDomains = async (domains) => {
    setSelectedDomains(domains);
    sessionStorage.setItem("userData.interests", JSON.stringify(domains));
    console.log(domains);
    // 관심사 변경 API 호출
    const response = await updateInterests(domains);
    if (response.success) {
      alert("✅ 관심사가 성공적으로 변경되었습니다!");
      setIsDomainModalOpen(false);
    } else {
      alert("❌ 관심사 변경 실패: " + response.message);
    }
  };

  // 주력 언어 모달 열기
  const handleOpenLanguageModal = () => {
    setIsLanguageModalOpen(true);
  };

  // 주력 언어 업데이트
  const handleSaveLanguages = async (languages) => {
    setSelectedLanguages(languages);
    sessionStorage.setItem("userData.languages", JSON.stringify(languages));

    // 🔥 주력 언어 변경 API 호출 (기능 추가)
    const response = await updateLanguages(languages);
    if (response.success) {
      alert("✅ 주력 언어가 성공적으로 변경되었습니다!");
      setIsLanguageModalOpen(false);
    } else {
      alert("❌ 주력 언어 변경 실패: " + response.message);
    }
  };

  // 최신 사용자 정보를 가져오는 함수
  const fetchUserInfo = async () => {
    try {
      const response = await myInfo();
      if (response.success) {
        const userData = response.data;
        setProfileImage(userData.profileImage);
        sessionStorage.setItem("userData.profileImage", userData.profileImage);
      }
    } catch (error) {
      console.error("❌ 사용자 정보 불러오기 실패:", error);
    }
  };

  // 마운트 시 사용자 정보 로드
  useEffect(() => {
    fetchUserInfo();
  }, [profileImage]);

  // 프로필 편집 모드 시작 시 기존 이미지 저장
  const handleEditStart = () => {
    const storedProfileImage = sessionStorage.getItem("userData.profileImage");
    setPrevProfileImage(storedProfileImage || profileImage); // 세션 스토리지의 이미지 저장
    setIsEditing(true);
  };

  // 프로필 이미지 변경 핸들러
  const handleProfileImageUpload = async (event) => {
    const file = event.target.files[0];
    const maxSize = 1024 * 1024; // 1MB (5 * 1024 * 1024 바이트)
    const allowedTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"];

    if (file.size > maxSize) {
      alert("파일 크기가 1MB를 초과할 수 없습니다.");
      return;
    }

    // 파일 타입 체크
    if (!allowedTypes.includes(file.type)) {
      alert("허용된 이미지 형식(jpg, png, gif, webp)만 업로드할 수 있습니다.");
      return;
    }

    // 이미지 미리보기 생성
    const reader = new FileReader();
    reader.onload = () => {
      setProfileImage(reader.result); // 이미지 미리보기 적용
    };
    reader.readAsDataURL(file);

    // 프로필 이미지 서버 업데이트 요청
    try {
      const result = await updateProfileImage(file);
      if (result.success) {
        const newProfileImage = result.data.profileImage;
        setProfileImage(newProfileImage);
        sessionStorage.setItem("userData.profileImage", newProfileImage);
      } else {
        alert("프로필 이미지 변경에 실패했습니다.");
      }
    } catch (error) {
      console.error("❌ 프로필 이미지 업로드 실패:", error);
    }
  };

  // 취소 버튼 클릭 핸들러
  const handleCancelEdit = () => {
    setProfileImage(
      prevProfileImage || sessionStorage.getItem("userData.profileImage")
    ); // 원래 이미지로 복구
    setNickname(initialNickname); // 이전 상태로 복원
    setNicknameError("");
    setIsEditing(false); // 편집 모드 종료
  };

  // 프로필 이미지 삭제 핸들러 (기본 이미지로 변경)
  const handleProfileImageRemove = async () => {
    try {
      setProfileImage(defaultProfileImage);
      sessionStorage.setItem("userData.profileImage", defaultProfileImage);

      // 서버에서 null을 기본 이미지로 처리하도록 요청
      const result = await updateProfileImage(null);

      if (!result.success) {
        alert("❌ 프로필 이미지를 기본값으로 변경하는데 실패했습니다.");
        setProfileImage(prevProfileImage);
      }
    } catch (error) {
      console.error("❌ 프로필 이미지 삭제 실패:", error);
    }
  };

  // 닉네임 글자 수 제한
  const handleNicknameChange = (e) => {
    const newNickname = e.target.value;

    if (newNickname.length > 15) {
      setNicknameError("닉네임은 최대 15글자까지 가능합니다.");
      return;
    } else {
      setNicknameError(""); // 글자 수가 줄어들면 오류 메시지 제거
    }

    setNickname(newNickname);
  };

  // 프로필 편집
  const handleEditToggle = async () => {
    if (isEditing) {
      if (nickname.length < 2) {
        setNicknameError("닉네임은 최소 2글자 이상이어야 합니다.");
        return;
      }

      try {
        const response = await updateNickname(nickname); // 닉네임 변경 API 호출

        if (response.success) {
          const newNickname = response.data.nickname;
          setNickname(newNickname); // ✅ 즉시 UI에 반영
          sessionStorage.setItem("userData.nickname", newNickname);
          setIsEditing(false); // ✅ 편집 모드 종료
        } else {
          alert("프로필 수정 실패");
          setNickname(initialNickname); // 닉네임을 기존 값으로 되돌림
          setProfileImage(prevProfileImage);
          setIsEditing(false);
        }
      } catch (error) {
        console.error("❌ 프로필 수정 실패:", error);
        setNickname(initialNickname); // 오류 발생 시 기존 닉네임으로 복원
        setIsEditing(false);
      }
    } else {
      setIsEditing(true);
    }
  };

  useEffect(() => {
    const storedInterests = sessionStorage.getItem("userData.interests");
    const storedLanguages = sessionStorage.getItem("userData.languages");

    // ✅ JSON 파싱 후, 값이 없거나 `null`이면 빈 배열 `[]`을 기본값으로 설정
    try {
      setSelectedDomains(storedInterests ? JSON.parse(storedInterests) : []);
    } catch (error) {
      setSelectedDomains([]); // JSON 파싱 오류 발생 시 안전하게 빈 배열로 설정
    }

    try {
      setSelectedLanguages(storedLanguages ? JSON.parse(storedLanguages) : []);
    } catch (error) {
      setSelectedLanguages([]);
    }
  }, []);

  return (
    <div className="profile-container">
      <div className="profile-header">
        <div className="profile-image-wrapper">
          <img src={profileImage} alt={nickname} className="profile-image" />
          {isEditing && (
            <div className="profile-image-buttons">
              {/* <label onClick={handleProfileImageRemove} className="profile-image-delete-icon">
                <IconBlock />
              </label> */}
              <label
                htmlFor="profile-upload"
                className="profile-image-edit-icon"
              >
                <IconAdd />
              </label>
              <input
                type="file"
                id="profile-upload"
                style={{ display: "none" }}
                onChange={handleProfileImageUpload}
                accept="image/*"
              />
            </div>
          )}
        </div>
        <div className="profile-header-info">
          <div className="profile-nickname-username">
            <div className="nickname-edit-profile-btn">
              {isEditing ? (
                <>
                  <input
                    type="text"
                    value={nickname}
                    onChange={handleNicknameChange}
                    className="nickname-input"
                  />
                </>
              ) : (
                <span>{nickname}</span>
              )}

              {isEditing ? (
                <>
                  <button
                    className="edit-profile-btn"
                    onClick={handleEditToggle}
                  >
                    완료
                  </button>
                  {/* <button className="cancel-profile-btn" onClick={handleCancelEdit}>
                    취소
                  </button> */}
                </>
              ) : (
                <>
                  <button
                    className="edit-profile-btn"
                    onClick={handleEditStart}
                  >
                    프로필 편집
                  </button>
                </>
              )}
              <p
                className={`nickname-error-message ${
                  nicknameError ? "visible" : "hidden"
                }`}
              >
                {nicknameError}
              </p>
            </div>
            <div className="username">
              <p className="username-title">사용자명</p>
              <p className="username-content">
                {nickname}@{memberId}
              </p>
            </div>
          </div>
          <div className="interests-languages">
            <div className="interests">
              <span>관심 도메인</span>
              {selectedDomains !== null &&
                selectedDomains.map((domain) => (
                  <Badge key={domain} type={domain} color="gray" />
                ))}
              {isEditing && (
                <div className="clickable-icon" onClick={handleOpenDomainModal}>
                  <IconAdd />
                </div>
              )}
            </div>
            <div className="languages">
              <span>사용 언어</span>
              {selectedLanguages !== null &&
                selectedLanguages.map((language) => (
                  <Badge key={language} type={language} color="gray" />
                ))}
              {isEditing && (
                <div
                  className="clickable-icon"
                  onClick={handleOpenLanguageModal}
                >
                  <IconAdd />
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      <p className="profile-details-title">프로필</p>
      <div className="profile-details">
        <div className="profile-item">
          <h2>별명</h2>
          <p className="nickname">{nickname}</p>
        </div>
        <hr></hr>
        <div className="profile-item">
          <h2>친구 추가 ID</h2>
          <p>
            {nickname}@{memberId}
          </p>
        </div>
        <hr></hr>
        <div className="profile-item">
          <h2>비밀번호</h2>
          <p>********</p>
        </div>
      </div>
      {isDomainModalOpen && (
        <ModalSelectDomain
          onClose={() => setIsDomainModalOpen(false)}
          onSave={handleSaveDomains}
          selectedDomains={selectedDomains}
        />
      )}
      {isLanguageModalOpen && (
        <ModalSelectLanguage
          onClose={() => setIsLanguageModalOpen(false)}
          onSave={handleSaveLanguages}
          selectedLanguages={selectedLanguages}
        />
      )}
    </div>
  );
}

export default Profile;
