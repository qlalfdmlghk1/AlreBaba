import React, { useState, useEffect } from "react";
import { toast } from "react-toastify";
import {
  createStudy,
  uploadImage,
  myStudy,
  studyDetail,
  updateStudyName,
} from "../../service/study";

import uploadSvg from "../../assets/upload.svg";
import uploadBorderSvg from "../../assets/upload-border.svg";

import "./ModalBase.css";
import "./ModalCreateStudy.css";
import { validateImage } from "../../service/member";

function ModalCreateStudy({ onClose, studyId }) {
  const [studyName, setStudyName] = useState("");
  const [imageSrc, setImageSrc] = useState(uploadSvg);

  const [uploadFile, setUploadFile] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 이미지 선택 핸들러
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    const maxSize = 1024 * 1024; // 1MB (5 * 1024 * 1024 바이트)
    const allowedTypes = ["image/jpeg", "image/png", "image/webp"];

    if (file.size > maxSize) {
      toast.error("파일 크기가 1MB를 초과할 수 없습니다.");
      return;
    }

    // 파일 타입 체크
    if (!allowedTypes.includes(file.type)) {
      toast.error("허용된 이미지 형식(jpg, png, webp)만 업로드할 수 있습니다.");
      return;
    }

    if (file) {
      validateImage(file)
        .then(() => {
          const reader = new FileReader();
          reader.onload = () => {
            setImageSrc(reader.result);
          };
          reader.readAsDataURL(file);

          setUploadFile(file);
          console.log(file);
        })
        .catch(() => {
          toast.error("이미지 파일만 업로드할 수 있습니다.");
        });
    }
  };

  // studyname 입력 핸들러
  const handleStudyNameChange = (e) => {
    setStudyName(e.target.value);
  };

  // studyname 길이 확인 (2~15자 사이)
  const isStudyNameValid = studyName.length > 1 && studyName.length <= 15;

  // 스터디 생성 핸들러
  const handleCreateStudy = async () => {
    if (!isStudyNameValid || isSubmitting) return;

    setIsSubmitting(true);
    const toastId = toast.loading("스터디 생성 중...");

    try {
      const result = await createStudy(studyName);
      if (imageSrc !== uploadSvg && uploadFile) {
        await uploadImage(result.data.studyId, uploadFile);
      }

      if (result.success) {
        await myStudy();
        toast.dismiss(toastId);
        toast.success("스터디가 성공적으로 생성되었습니다.");
        onClose();
      } else {
        toast.dismiss(toastId);
        toast.error("스터디 생성에 실패했습니다. 다시 시도해주세요.");
      }
    } catch (error) {
      console.error("스터디 생성 중 오류 발생:", error);
      toast.dismiss(toastId);
      toast.error("스터디 생성에 실패했습니다. 다시 시도해주세요.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // 스터디 수정 핸들러
  const handleUpdateStudy = async () => {
    if (!isStudyNameValid || !studyId || isSubmitting) return;

    setIsSubmitting(true);
    const toastId = toast.loading("스터디 수정 중...");

    try {
      if (uploadFile) {
        await validateImage(uploadFile);
        await uploadImage(studyId, uploadFile);
      }

      const result = await updateStudyName(studyId, studyName);

      if (result.success) {
        await myStudy();
        toast.dismiss(toastId);
        toast.success("스터디가 성공적으로 수정되었습니다.");
        onClose();
      } else {
        toast.dismiss(toastId);
        toast.error("스터디 수정에 실패했습니다. 다시 시도해주세요.");
      }
    } catch (error) {
      console.error("스터디 수정 중 오류 발생:", error);
      toast.dismiss(toastId);
      toast.error("스터디 수정에 실패했습니다. 다시 시도해주세요.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // studyId가 존재하면 기존 스터디 정보를 불러옴
  useEffect(() => {
    if (studyId) {
      const fetchStudyDetail = async () => {
        try {
          const result = await studyDetail(studyId);
          console.log(result);
          if (result.success) {
            setStudyName(result.data.studyName);
            setImageSrc(result.data.imageUrl || uploadSvg);
          }
        } catch (error) {
          console.error("스터디 정보를 불러오는 중 오류 발생:", error);
        }
      };
      fetchStudyDetail();
    }
  }, [studyId]);

  return (
    <div className="container" onMouseDown={onClose}>
      <div className="modal" onMouseDown={(e) => e.stopPropagation()}>
        {!studyId ? (
          <div className="header">스터디를 생성해주세요</div>
        ) : (
          <div className="header">스터디 정보 편집</div>
        )}

        <div>
          <label htmlFor="image-upload">
            <div className="upload-container">
              <img
                src={imageSrc}
                alt="Upload Preview"
                className={`upload-preview ${
                  imageSrc !== uploadSvg ? "rounded" : ""
                }`}
              />

              {imageSrc !== uploadSvg && (
                <img
                  src={uploadBorderSvg}
                  alt="Upload Border"
                  className="upload-border"
                />
              )}
            </div>
          </label>
          <input
            id="image-upload"
            type="file"
            accept="image/*"
            style={{ display: "none" }}
            onChange={handleImageChange}
          />
        </div>
        <div className="input-container">
          <div className="input-container-title">
            <label>스터디 이름</label>
            {/* 유효성 검사 메시지 */}
            {!isStudyNameValid && (
              <span className="error-message">
                {studyName.length < 2
                  ? "2글자 이상 입력해주세요."
                  : "15글자까지 가능합니다."}
              </span>
            )}
          </div>
          <input
            id="studyname"
            type="text"
            placeholder="스터디 이름을 작성해주세요."
            value={studyName}
            onChange={handleStudyNameChange}
            maxLength={15}
          />
        </div>
        <div className="button-container">
          <button className="cancel" onClick={onClose}>
            취소
          </button>
          {/* studyname 유효성 검사 - 버튼 활성화/비활성화 */}
          {studyId ? (
            <button
              className="create"
              onClick={handleUpdateStudy}
              disabled={!isStudyNameValid}
            >
              수정
            </button>
          ) : (
            <button
              className="create"
              onClick={handleCreateStudy}
              disabled={!isStudyNameValid}
            >
              생성
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default ModalCreateStudy;
