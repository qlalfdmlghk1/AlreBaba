import React, { useEffect, useState } from "react";
// 도메인 아이콘 import
import IconArtificialIntelligence from "../Icons/IconArtificialIntelligence";
import IconCommunity from "../Icons/IconCommunity";
import IconEcommerceShopping from "../Icons/IconEcommerceShopping";
import IconEducation from "../Icons/IconEducation";
import IconFinance from "../Icons/IconFinance";
import IconGame from "../Icons/IconGame";
import IconHardwareEmbedded from "../Icons/IconHardwareEmbedded";
import IconManufacturing from "../Icons/IconManufacturing";
import IconMedicalHealthcare from "../Icons/IconMedicalHealthcare";
import IconPortalSocialMedia from "../Icons/IconPortalSocialMedia";
import IconPublicSector from "../Icons/IconPublicSector";
import IconSecurityVaccine from "../Icons/IconSecurityVaccine";
import IconTelecommunicationNetwork from "../Icons/IconTelecommunicationNetwork";
import "./ModalBase.css";
import "./ModalSelectDomain.css";

function ModalSelectDomain({ onClose, onSave, selectedDomains }) {
  const [domains, setDomains] = useState([]);

  useEffect(() => {
    setDomains(selectedDomains);
  }, [selectedDomains]);

  const toggleDomain = (domainId) => {
    setDomains((prev) => {
      const safePrev = prev || []; // prev가 null이면 빈 배열 사용
      return safePrev.includes(domainId)
        ? safePrev.filter((id) => id !== domainId)
        : safePrev.length < 5
        ? [...safePrev, domainId]
        : safePrev;
    });
  };

  const domainList = [
    {
      name: "인공지능",
      id: "ARTIFICIAL_INTELLIGENCE",
      icon: <IconArtificialIntelligence width={60} height={60} />,
    },
    {
      name: "커뮤니티",
      id: "COMMUNITY",
      icon: <IconCommunity width={60} height={60} />,
    },
    {
      name: "이커머스",
      id: "ECOMMERCE_SHOPPING",
      icon: <IconEcommerceShopping width={60} height={60} />,
    },
    {
      name: "교육",
      id: "EDUCATION",
      icon: <IconEducation width={60} height={60} />,
    },
    {
      name: "금융",
      id: "FINANCE",
      icon: <IconFinance width={60} height={60} />,
    },
    { name: "게임", id: "GAME", icon: <IconGame width={60} height={60} /> },
    {
      name: "임베디드",
      id: "HARDWARE_EMBEDDED",
      icon: <IconHardwareEmbedded width={60} height={60} />,
    },
    {
      name: "제조",
      id: "MANUFACTURING",
      icon: <IconManufacturing width={60} height={60} />,
    },
    {
      name: "공공",
      id: "PUBLIC_SECTOR",
      icon: <IconPublicSector width={60} height={60} />,
    },
    {
      name: "보안",
      id: "SECURITY_VACCINE",
      icon: <IconSecurityVaccine width={60} height={60} />,
    },
    {
      name: "통신",
      id: "TELECOMMUNICATION_NETWORK",
      icon: <IconTelecommunicationNetwork width={60} height={60} />,
    },
    {
      name: "의료/헬스케어",
      id: "MEDICAL_HEALTHCARE",
      icon: <IconMedicalHealthcare width={60} height={60} />,
    },
    {
      name: "소셜미디어",
      id: "PORTAL_SOCIAL_MEDIA",
      icon: <IconPortalSocialMedia width={60} height={60} />,
    },
  ];

  return (
    <div className="container">
      <div className="modal">
        <div className="header">
          관심 도메인을 선택해주세요
          <br />
          <div className="explane">최대 5개까지 선택 가능합니다.</div>
        </div>
        <div className="domains">
          {domainList.map((domain) => (
            <div
              key={domain.name}
              className={`domain ${
                domains.includes(domain.name) ? "selected" : ""
              }`}
              onClick={() => toggleDomain(domain.name)}
            >
              {domain.name}
              <div className="icon">{domain.icon}</div>
            </div>
          ))}
        </div>
        <div className="button-container">
          <button className="cancel" onClick={onClose}>
            취소
          </button>
          <button className="create" onClick={() => onSave(domains)}>
            확인
          </button>
        </div>
      </div>
    </div>
  );
}

export default ModalSelectDomain;
