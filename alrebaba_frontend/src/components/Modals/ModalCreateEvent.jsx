import React, { useState, useRef, useEffect } from "react";
import { toast } from "react-toastify";
import { createEvent, updateEvent, deleteEvent } from "../../service/event";

import Calendar from "react-calendar";
import IconAlarm from "../Icons/IconAlarm";
import prevButton from "../../assets/images/button-arrow-left.png";
import nextButton from "../../assets/images/button-arrow-right.png";

import "react-calendar/dist/Calendar.css";
import "./ModalBase.css";
import "./ModalCreateEvent.css";

// 12시간(오전/오후)를 24시간으로 변환하는 함수
const convertTo24Hour = (hour, ampm) => {
  if (ampm === "오후") {
    return hour === 12 ? 12 : hour + 12;
  } else {
    return hour === "오전" && hour === 12 ? 0 : hour === 12 ? 0 : hour;
  }
};

function ModalCreateEvent({ date, setDate, closeModal, selectedEvent }) {
  const handleDateChange = (newDate) => {
    setDate(newDate);
  };

  const [page, setPage] = useState(1);

  const handlePrev = () => {
    if (page > 1) {
      setPage(page - 1);
    }
  };

  const handleNext = () => {
    if (page < 3) {
      setPage(page + 1);
    }
  };

  const [isTimePickerOpen, setIsTimePickerOpen] = useState(false);
  const [isTimeSetterOpen, setIsTimeSetterOpen] = useState(false);
  const [isAlarmSetterOpen, setIsAlarmSetterOpen] = useState(false);
  const [useAlarm, setUseAlarm] = useState(false);
  const [eventName, setEventName] = useState("");
  const [eventDescription, setEventDescription] = useState("");
  const [selectedColor, setSelectedColor] = useState("#000000");

  const [selectedAMPM, setSelectedAMPM] = useState("오전");
  const [selectedHour, setSelectedHour] = useState(12);
  const [selectedMinute, setSelectedMinute] = useState(0);
  const [selectedDurationHour, setDurationHour] = useState(0);
  const [selectedDurationMinute, setDurationMinute] = useState(0);
  const [selectedAlarmMinute, setAlarmMinute] = useState(0);

  const pickerRef = useRef(null);
  const setterRef = useRef(null);
  const alarmRef = useRef(null);

  // 팝업 외부 클릭 감지
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (pickerRef.current && !pickerRef.current.contains(event.target)) {
        setIsTimePickerOpen(false);
      }
      if (setterRef.current && !setterRef.current.contains(event.target)) {
        setIsTimeSetterOpen(false);
      }
      if (alarmRef.current && !alarmRef.current.contains(event.target)) {
        setIsAlarmSetterOpen(false);
      }
    };
    document.addEventListener("click", handleClickOutside);
    return () => {
      document.removeEventListener("click", handleClickOutside);
    };
  }, []);

  useEffect(() => {
    setIsTimePickerOpen(false);
    setIsTimeSetterOpen(false);
    setIsAlarmSetterOpen(false);
  }, [selectedMinute, selectedDurationMinute, selectedAlarmMinute]);

  useEffect(() => {
    if (selectedEvent) {
      console.log(selectedEvent);
      setEventName(selectedEvent.eventName || "새로운 이벤트");
      setEventDescription(selectedEvent.description || "");
      setSelectedColor(selectedEvent.colorCode || "#000000");

      const [hour, minute] = selectedEvent.startTime.split(":").map(Number);
      const isPM = hour >= 12;
      setSelectedAMPM(isPM ? "오후" : "오전");
      setSelectedHour(
        isPM ? (hour === 12 ? 12 : hour - 12) : hour === 0 ? 12 : hour
      );
      setSelectedMinute(minute);

      setDurationHour(selectedEvent.durationHours || 0);
      setDurationMinute(selectedEvent.durationMinutes || 0);

      setPage(3);

      if (selectedEvent.remindBeforeMinutes !== null) {
        setUseAlarm(true);
        setAlarmMinute(selectedEvent.remindBeforeMinutes);
      } else {
        setUseAlarm(false);
        setAlarmMinute(0);
      }
    }
  }, [selectedEvent]);

  // 시작 시간 표시
  function getTimeDisplay() {
    return `${selectedAMPM} ${String(selectedHour).padStart(2, "0")}:${String(
      selectedMinute
    ).padStart(2, "0")}`;
  }

  // 지속 시간 표시
  function getDurationTimeDisplay() {
    return `${String(selectedDurationHour).padStart(2, "0")}시간 ${String(
      selectedDurationMinute
    ).padStart(2, "0")}분`;
  }

  // 알림 상태 표시
  function getAlarmState() {
    return useAlarm ? "사용함" : "사용하지 않음";
  }

  // 알림 시간 표시
  function getAlarmTimeDisplay() {
    return `${String(selectedAlarmMinute).padStart(2, "0")}분 전`;
  }

  // 이벤트 명 핸들러
  const handleEventNameChange = (e) => {
    const value = e.target.value;
    // 유효성 체크 15자 이하
    if (value.length <= 15) {
      setEventName(value);
    }
  };

  // 이벤트 설명 핸들러
  const handleEventDescriptionChange = (e) => {
    const value = e.target.value;
    // 유효성 체크 100자 이하
    if (value.length <= 100) {
      setEventDescription(value);
    }
  };

  // 이벤트 색상 핸들러
  const handleColorChange = (e) => {
    setSelectedColor(e.target.value);
  };

  // 이벤트 생성
  const handleSubmit = async () => {
    // 12시간 포맷을 24시간 포맷으로 변환
    const hour24 =
      selectedAMPM === "오후"
        ? selectedHour === 12
          ? 12
          : selectedHour + 12
        : selectedHour === 12
        ? 0
        : selectedHour;

    // 날짜, 시간 합치기
    const startTime = `${date.getFullYear()}-${String(
      date.getMonth() + 1
    ).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(
      hour24
    ).padStart(2, "0")}:${String(selectedMinute).padStart(2, "0")}`;

    const eventData = {
      eventName,
      description: eventDescription,
      startTime,
      durationHours: selectedDurationHour,
      durationMinutes: selectedDurationMinute,
      color: selectedColor,
    };

    // useAlarm이 true일 경우에만 remindBeforeMinutes 추가
    if (useAlarm) {
      eventData.remindBeforeMinutes = selectedAlarmMinute;
    }

    // 디버깅 로그
    // console.log("클라이언트 현재 시간 (Asia/Seoul):", new Date().toLocaleString("ko-KR", { timeZone: "Asia/Seoul" }));
    // console.log("생성 요청 startTime:", startTime);

    const result = await createEvent(eventData);

    if (result.success) {
      toast.success("이벤트가 성공적으로 생성되었습니다.");
      closeModal();
    } else {
      if (result.error === "startTime") {
        setPage(1);
        toast.error(result.message);
      } else if (
        result.error === "eventName" ||
        result.error === "description"
      ) {
        setPage(2);
        toast.error(result.message);
      } else {
        toast.error(result.message || "이벤트 생성을 실패했했습니다.");
      }
    }
  };

  // 이벤트 수정
  const handleUpdate = async (eventId) => {
    // 12시간 포맷을 24시간 포맷으로 변환
    const hour24 =
      selectedAMPM === "오후"
        ? selectedHour === 12
          ? 12
          : selectedHour + 12
        : selectedHour === 12
        ? 0
        : selectedHour;

    const startTime = `${date.getFullYear()}-${String(
      date.getMonth() + 1
    ).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(
      hour24
    ).padStart(2, "0")}:${String(selectedMinute).padStart(2, "0")}`;

    const eventData = {
      eventName,
      description: eventDescription,
      startTime,
      durationHours: selectedDurationHour,
      durationMinutes: selectedDurationMinute,
      color: selectedColor,
    };

    if (useAlarm) {
      eventData.remindBeforeMinutes = selectedAlarmMinute;
    }

    const result = await updateEvent(eventId, eventData);

    if (result.success) {
      toast.success("이벤트가 성공적으로 수정되었습니다.");
      closeModal();
    } else {
      // 수정 실패 처리
    }
  };

  // 이벤트 삭제
  const handleDelete = async (eventId) => {
    const result = await deleteEvent(eventId);

    if (result.success) {
      toast.success("이벤트가 성공적으로 삭제되었습니다.");
      closeModal();
    } else {
      toast.error(result.message || "이벤트 삭제를 실패했습니다.");
    }
  };

  return (
    <div className="container" onMouseDown={closeModal}>
      <div
        className="modal create-event"
        onMouseDown={(e) => e.stopPropagation()}
      >
        {/* 이전 버튼 */}
        <div>
          {(selectedEvent === null ||
            selectedEvent.createdBy ==
              sessionStorage.getItem("userData.memberId")) &&
            page !== 1 && (
              <img
                src={prevButton}
                alt="이전"
                className="prev-btn"
                onClick={handlePrev}
              />
            )}
        </div>
        {/* 이벤트 시간 설정 */}
        {page === 1 && (
          <div>
            <div className="progress-container">
              <div className="progress-bar">
                <div className="in-progress"> </div>
                이벤트 시간
              </div>
              <div className="progress-bar">
                <div className="yet"> </div>
                이벤트 정보
              </div>
              <div className="progress-bar">
                <div className="yet"> </div>
                검토하기
              </div>
            </div>
            <div className="modal-calendar-wrapper">
              <Calendar
                onChange={handleDateChange}
                value={date}
                view="month"
                locale="en-US"
                selectRange={false}
                minDate={new Date()}
              />
            </div>

            <div className="set-time">
              <div className="time-picker" ref={pickerRef}>
                <label>시작 시간</label>
                <div
                  id="time-input"
                  className="time-display"
                  onClick={() => setIsTimePickerOpen((prev) => !prev)}
                >
                  {getTimeDisplay()}
                </div>

                {isTimePickerOpen && (
                  <div
                    className="time-picker-popup"
                    style={{
                      bottom:
                        window.innerHeight -
                          (window.scrollY +
                            document.documentElement.scrollHeight) <
                        0
                          ? "auto" // 화면 아래로 넘어가면
                          : "100%", // 아니면 기본 위치
                      top:
                        window.innerHeight -
                          (window.scrollY +
                            document.documentElement.scrollHeight) <
                        0
                          ? "auto" // 화면이 꽉 차면 위로 뜨게
                          : "unset",
                    }}
                  >
                    {/* 오전/오후 선택 */}
                    <div className="scroll-container">
                      {["오전", "오후"].map((ampm) => (
                        <div
                          key={ampm}
                          className={`ampm-option ${
                            selectedAMPM === ampm ? "active" : ""
                          }`}
                          onClick={() => setSelectedAMPM(ampm)}
                        >
                          {ampm}
                        </div>
                      ))}
                    </div>

                    {/* 시 선택 */}
                    <div className="scroll-container">
                      {Array.from({ length: 12 }, (_, i) => i + 1).map(
                        (hour) => (
                          <div
                            key={hour}
                            className={`hour-option ${
                              selectedHour === hour ? "active" : ""
                            }`}
                            onClick={() => setSelectedHour(hour)}
                          >
                            {String(hour).padStart(2, "0")}
                          </div>
                        )
                      )}
                    </div>

                    {/* 분 선택 */}
                    <div className="scroll-container">
                      {Array.from({ length: 60 }, (_, i) => i).map((minute) => (
                        <div
                          key={minute}
                          className={`minute-option ${
                            selectedMinute === minute ? "active" : ""
                          }`}
                          onClick={() => setSelectedMinute(minute)}
                        >
                          {String(minute).padStart(2, "0")}
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
              <div className="time-setter" ref={setterRef}>
                <label>지속 시간</label>
                <div
                  id="time-input"
                  className="time-display"
                  onClick={() => setIsTimeSetterOpen((prev) => !prev)}
                >
                  {getDurationTimeDisplay()}
                </div>

                {isTimeSetterOpen && (
                  <div
                    className="time-setter-popup"
                    style={{
                      bottom:
                        window.innerHeight -
                          (window.scrollY +
                            document.documentElement.scrollHeight) <
                        0
                          ? "auto" // 화면 아래로 넘어가면
                          : "100%", // 아니면 기본 위치
                      top:
                        window.innerHeight -
                          (window.scrollY +
                            document.documentElement.scrollHeight) <
                        0
                          ? "auto" // 화면이 꽉 차면 위로 뜨게
                          : "unset",
                    }}
                  >
                    {/* 시 선택 */}
                    <div className="hour-container2">
                      <div className="scroll-container2">
                        {Array.from({ length: 12 }, (_, i) => i).map((hour) => (
                          <div
                            key={hour}
                            className={`hour-option2 ${
                              selectedDurationHour === hour ? "active" : ""
                            }`}
                            onClick={() => setDurationHour(hour)}
                          >
                            {String(hour).padStart(2, "0")}
                          </div>
                        ))}
                      </div>
                    </div>

                    <div className="minute-container2">
                      {/* 분 선택 */}
                      <div className="scroll-container2">
                        {Array.from({ length: 60 }, (_, i) => i).map(
                          (minute) => (
                            <div
                              key={minute}
                              className={`minute-option2 ${
                                selectedDurationMinute === minute
                                  ? "active"
                                  : ""
                              }`}
                              onClick={() => setDurationMinute(minute)}
                            >
                              {String(minute).padStart(2, "0")}
                            </div>
                          )
                        )}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
        {/* 이벤트 정보 설정 */}
        {page === 2 && (
          <div>
            <div className="progress-container">
              <div className="progress-bar">
                <div className="in-progress"> </div>
                이벤트 시간
              </div>
              <div className="progress-bar">
                <div className="in-progress"> </div>
                이벤트 정보
              </div>
              <div className="progress-bar">
                <div className="yet"> </div>
                검토하기
              </div>
            </div>
            <div className="time-setter">
              <div className="label-error-msg-container">
                <label>이벤트 명</label>
                {eventName.length < 2 && (
                  <div className="error-msg">2자 이상 입력해 주세요</div>
                )}
                {eventName.length === 15 && (
                  <div className="error-msg">15자 까지만 입력 가능합니다.</div>
                )}
              </div>
              <input
                id="eventname"
                type="text"
                placeholder="새로운 이벤트"
                value={eventName}
                onChange={handleEventNameChange}
              />
            </div>
            <div className="time-setter">
              <label htmlFor="colorPicker">색상 선택</label>
              <input
                id="colorPicker"
                type="color"
                value={selectedColor}
                onChange={handleColorChange}
                style={{ backgroundColor: selectedColor }}
              />
            </div>
            <div className="set-time">
              <div className="time-setter">
                <label>알림 설정</label>
                <div>
                  <div
                    id="time-input"
                    className="time-display"
                    onClick={() => setUseAlarm((prev) => !prev)}
                  >
                    {getAlarmState()}
                  </div>
                </div>
              </div>
              {useAlarm && (
                <div className="time-setter" ref={alarmRef}>
                  <label></label>
                  <div
                    id="time-input"
                    className="time-display"
                    onClick={() => setIsAlarmSetterOpen((prev) => !prev)}
                  >
                    {getAlarmTimeDisplay()}
                  </div>

                  {isAlarmSetterOpen && (
                    <div className="time-setter-popup">
                      <div className="minute-container2">
                        {/* 분 선택 */}
                        <div className="scroll-container2">
                          {Array.from({ length: 100 }, (_, i) => i).map(
                            (minute) => (
                              <div
                                key={minute}
                                className={`minute-option3 ${
                                  selectedAlarmMinute === minute ? "active" : ""
                                }`}
                                onClick={() => setAlarmMinute(minute)}
                              >
                                {String(minute).padStart(2, "0")}
                              </div>
                            )
                          )}
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>

            <div className="time-setter">
              <div className="label-error-msg-container">
                <label>이벤트 설명</label>
                {eventDescription.length === 100 && (
                  <div className="error-msg">100자 까지만 입력 가능합니다.</div>
                )}
              </div>
              <textarea
                id="eventname"
                type="text"
                placeholder="구성원들에게 이벤트에 대해 알려주세요."
                className="explain"
                value={eventDescription}
                onChange={handleEventDescriptionChange}
              />
            </div>
          </div>
        )}

        {/* 이벤트 검토 */}
        {page === 3 && (
          <div className="event-review-container">
            {!selectedEvent ||
            selectedEvent.createdBy ==
              sessionStorage.getItem("userData.memberId") ? (
              <div className="progress-container">
                <div className="progress-bar">
                  <div className="in-progress"> </div>
                  이벤트 시간
                </div>
                <div className="progress-bar">
                  <div className="in-progress"> </div>
                  이벤트 정보
                </div>
                <div className="progress-bar">
                  <div className="in-progress"> </div>
                  검토하기
                </div>
              </div>
            ) : (
              <div style={{ marginTop: "25px" }}></div>
            )}

            <div
              className="event-review"
              style={{ border: `${selectedColor} 2px solid` }}
            >
              <>
                <div className="header">
                  <div>
                    <div className="event-name">{eventName}</div>
                    <div className="time">
                      {date.toLocaleDateString() +
                        " " +
                        selectedAMPM +
                        " " +
                        String(selectedHour).padStart(2, "0") +
                        ":" +
                        String(selectedMinute).padStart(2, "0")}
                    </div>
                  </div>
                </div>
                <div className="event-description">{eventDescription}</div>
                <div className="alarm-info">
                  <IconAlarm />
                  {useAlarm ? selectedAlarmMinute + "분 전" : "사용하지 않음"}
                </div>
              </>
            </div>

            <div className="button-container">
              {selectedEvent ? (
                selectedEvent.createdBy ==
                sessionStorage.getItem("userData.memberId") ? (
                  <>
                    <button
                      className="cancel"
                      onClick={() => handleDelete(selectedEvent.eventId)}
                    >
                      삭제
                    </button>
                    <button
                      className="create"
                      onClick={() => handleUpdate(selectedEvent.eventId)}
                    >
                      수정
                    </button>
                  </>
                ) : null
              ) : (
                <button className="create" onClick={handleSubmit}>
                  생성
                </button>
              )}
            </div>
          </div>
        )}

        {/* 다음 버튼 */}
        <div>
          {page !== 3 && (
            <img
              src={nextButton}
              alt="다음"
              className="next-btn"
              onClick={handleNext}
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default ModalCreateEvent;
