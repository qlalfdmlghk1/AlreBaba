import React, { useState, useEffect } from "react";
import Calendar from "react-calendar";
import ModalCreateEvent from "./Modals/ModalCreateEvent";
import EventTag from "./EventTag";
import { getEvents, getEvent } from "../service/event";

import "react-calendar/dist/Calendar.css";
import "./Event.css";

function Event() {
  const [date, setDate] = useState(new Date());
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [MonthEvents, setMonthEvents] = useState([]);

  // 이벤트 가져오기
  const fetchEvents = async (date) => {
    try {
      const result = await getEvents(date);
      if (result.success) {
        // 날짜 포맷 변환
        const formattedEvents = result.data.map((event) => {
          const formattedDate = new Date(event.date).toLocaleDateString(
            "en-US",
            {
              year: "numeric",
              month: "long",
              day: "numeric",
            }
          );

          return {
            ...event,
            date: formattedDate,
          };
        });

        setMonthEvents(formattedEvents);
      } else {
        // console.error("이벤트 불러오기 실패:", result.message);
      }
    } catch (error) {
      console.error("이벤트 조회 중 오류:", error);
    }
  };

  // 이벤트 상세 조회
  const fetchEvent = async (eventId) => {
    const result = await getEvent(eventId);
    if (result.success) {
      // console.log(result.data);
      setDate(new Date(result.data.eventDate));
      setSelectedEvent(result.data);
      setIsModalOpen(true);
    } else {
      console.error("이벤트 상세 조회 실패:", result.message);
    }
  };

  const handleDateChange = async (newDate) => {
    setDate(newDate);
    fetchEvents(newDate, setMonthEvents);
  };

  const handleActiveStartDateChange = ({ activeStartDate }) => {
    if (activeStartDate) {
      setDate(activeStartDate);
      fetchEvents(activeStartDate);
    }
  };

  const handleContainerClick = () => {
    fetchEvents(date, setMonthEvents);
  };

  useEffect(() => {
    if (!isModalOpen) {
      fetchEvents(date, setMonthEvents);
    }
  }, [isModalOpen, date]);

  useEffect(() => {
    handleDateChange(date);
  }, []);

  useEffect(() => {
    // console.log(date);
    // console.log(MonthEvents);
  }, [MonthEvents]);

  // 날짜에 맞는 이벤트 가져오기
  const getEventForDate = (date) => {
    const formattedDate = new Date(date).toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
    const event = MonthEvents.find((e) => e.date === formattedDate);
    return event ? event.events : [];
  };

  return (
    <>
      {isModalOpen && (
        <ModalCreateEvent
          closeModal={() => {
            setIsModalOpen(false);
            setSelectedEvent(null);
          }}
          date={date}
          setDate={setDate}
          selectedEvent={selectedEvent}
        />
      )}

      <div className="event-container" onClick={handleContainerClick}>
        <Calendar
          onChange={handleDateChange}
          value={date}
          view="month"
          locale="en-US"
          selectRange={false}
          onClickDay={() => setIsModalOpen(true)}
          onActiveStartDateChange={handleActiveStartDateChange}
          tileContent={({ date }) => {
            const events = getEventForDate(date);
            return (
              <EventTag date={date} events={events} onClick={fetchEvent} />
            );
          }}
          minDate={new Date()}
        />
      </div>
    </>
  );
}

export default Event;
