import React from "react";

// 도메인 아이콘 import
import IconArtificialIntelligence from "./Icons/IconArtificialIntelligence";
import IconCommunity from "./Icons/IconCommunity";
import IconEcommerceShopping from "./Icons/IconEcommerceShopping";
import IconEducation from "./Icons/IconEducation";
import IconFinance from "./Icons/IconFinance";
import IconGame from "./Icons/IconGame";
import IconHardwareEmbedded from "./Icons/IconHardwareEmbedded";
import IconManufacturing from "./Icons/IconManufacturing";
import IconMedicalHealthcare from "./Icons/IconMedicalHealthcare";
import IconPortalSocialMedia from "./Icons/IconPortalSocialMedia";
import IconPublicSector from "./Icons/IconPublicSector";
import IconSecurityVaccine from "./Icons/IconSecurityVaccine";
import IconTelecommunicationNetwork from "./Icons/IconTelecommunicationNetwork";

// 프로그래밍 언어 아이콘 import
import IconC from "./Icons/IconC";
import IconCPP from "./Icons/IconCPP";
import IconCS from "./Icons/IconCS";
import IconDart from "./Icons/IconDart";
import IconFortran from "./Icons/IconFortran";
import IconGo from "./Icons/IconGo";
import IconJava from "./Icons/IconJava";
import IconJavaScript from "./Icons/IconJavaScript";
import IconKotlin from "./Icons/IconKotlin";
import IconLua from "./Icons/IconLua";
import IconPHP from "./Icons/IconPHP";
import IconPython from "./Icons/IconPython";
import IconR from "./Icons/IconR";
import IconRuby from "./Icons/IconRuby";
import IconRust from "./Icons/IconRust";
import IconSQL from "./Icons/IconSQL";
import IconSwift from "./Icons/IconSwift";
import IconTypeScript from "./Icons/IconTypeScript";

import "./Badge.css";

function Badge({
  size = 16,
  type = "artificialIntelligence",
  color = "black",
  backgroundColor = "#f4f4f4",
}) {
  return (
    <div
      className="badge"
      style={{
        fontSize: `${size}px`,
        borderRadius: `${size}px`,
        padding: `${size / 4}px ${size / 2}px`,
        gap: `${size / 2}px`,
        color: color,
        backgroundColor: backgroundColor,
        whiteSpace: "nowrap",
      }}
    >
      {/* 도메인 아이콘 */}
      {type === "인공지능" && (
        <>
          <IconArtificialIntelligence width={size} height={size} /> 인공지능
        </>
      )}
      {type === "커뮤니티" && (
        <>
          <IconCommunity width={size} height={size} /> 커뮤니티
        </>
      )}
      {type === "이커머스" && (
        <>
          <IconEcommerceShopping width={size} height={size} /> 이커머스
        </>
      )}
      {type === "교육" && (
        <>
          <IconEducation width={size} height={size} /> 교육
        </>
      )}
      {type === "금융" && (
        <>
          <IconFinance width={size} height={size} /> 금융
        </>
      )}
      {type === "게임" && (
        <>
          <IconGame width={size} height={size} /> 게임
        </>
      )}
      {type === "임베디드" && (
        <>
          <IconHardwareEmbedded width={size} height={size} /> 임베디드
        </>
      )}
      {type === "제조" && (
        <>
          <IconManufacturing width={size} height={size} /> 제조
        </>
      )}
      {type === "의료/헬스케어" && (
        <>
          <IconMedicalHealthcare width={size} height={size} /> 의료/헬스케어
        </>
      )}
      {type === "소셜미디어" && (
        <>
          <IconPortalSocialMedia width={size} height={size} /> 소셜미디어
        </>
      )}
      {type === "공공" && (
        <>
          <IconPublicSector width={size} height={size} /> 공공
        </>
      )}
      {type === "보안" && (
        <>
          <IconSecurityVaccine width={size} height={size} /> 보안
        </>
      )}
      {type === "통신" && (
        <>
          <IconTelecommunicationNetwork width={size} height={size} /> 통신
        </>
      )}

      {/* 프로그래밍 언어 아이콘 */}
      {type === "C" && (
        <>
          <IconC width={size} height={size} /> C언어
        </>
      )}
      {type === "C_PlusPlus" && (
        <>
          <IconCPP width={size} height={size} /> C++
        </>
      )}
      {type === "C_Sharp" && (
        <>
          <IconCS width={size} height={size} /> C#
        </>
      )}
      {type === "Dart" && (
        <>
          <IconDart width={size} height={size} /> Dart
        </>
      )}
      {type === "Fortran" && (
        <>
          <IconFortran width={size} height={size} /> Fortran
        </>
      )}
      {type === "Go" && (
        <>
          <IconGo width={size} height={size} /> Go
        </>
      )}
      {type === "Java" && (
        <>
          <IconJava width={size} height={size} /> Java
        </>
      )}
      {type === "JavaScript" && (
        <>
          <IconJavaScript width={size} height={size} /> JavaScript
        </>
      )}
      {type === "kotlin" && (
        <>
          <IconKotlin width={size} height={size} /> Kotlin
        </>
      )}
      {type === "Lua" && (
        <>
          <IconLua width={size} height={size} /> Lua
        </>
      )}
      {type === "PHP" && (
        <>
          <IconPHP width={size} height={size} /> PHP
        </>
      )}
      {type === "Python" && (
        <>
          <IconPython width={size} height={size} /> Python
        </>
      )}
      {type === "R" && (
        <>
          <IconR width={size} height={size} /> R
        </>
      )}
      {type === "Rubt" && (
        <>
          <IconRuby width={size} height={size} /> Rubt
        </>
      )}
      {type === "Rust" && (
        <>
          <IconRust width={size} height={size} /> Rust
        </>
      )}
      {type === "Ruby" && (
        <>
          <IconRuby width={size} height={size} /> Ruby
        </>
      )}
      {type === "SQL" && (
        <>
          <IconSQL width={size} height={size} /> SQL
        </>
      )}
      {type === "Swift" && (
        <>
          <IconSwift width={size} height={size} /> Swift
        </>
      )}
      {type === "TypeScript" && (
        <>
          <IconTypeScript width={size} height={size} /> TypeScript
        </>
      )}
    </div>
  );
}

export default Badge;
