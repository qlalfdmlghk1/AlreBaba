import { useState } from "react";
import "./CodingTestStart.css";
import ModalCreateCodingTest from "./Modals/ModalCreateCodingTest";
import { useNavigate, useParams } from "react-router-dom";
import { getCTInfo, getCTProblems } from "../service/codingTest";

function CodingTestStart({ status, isLoading }) {
  const { channelId } = useParams();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();

  function handleOpenModal() {
    setIsModalOpen(true);
  }

  async function handleEnter() {
    const response1 = await getCTInfo({ channelId });
    const codingTestId = response1.data[0].codingTestId;
    const response = await getCTProblems(codingTestId);

    sessionStorage.setItem(`channelId-${channelId}`, codingTestId);

    navigate(`${codingTestId}/${response.data[0].problemId}`, {
      replace: true,
    });
  }

  function handleCloseModal() {
    setIsModalOpen(false);
  }

  return (
    <>
      {isLoading && (
        <div className="ct-page-loading">코딩테스트를 불러오고 있습니다...</div>
      )}
      {!isLoading && (
        <>
          {isModalOpen && (
            <ModalCreateCodingTest
              open={isModalOpen}
              onClose={handleCloseModal}
            />
          )}
          {status === "NOT_YET" && (
            <section className="coding-test-start">
              <h2>생성된 코딩 테스트가 없습니다.</h2>
              <div className="coding-test-start-question">
                <h4>모의 코딩 테스트를 생성하시겠습니까?</h4>
                <button onClick={handleOpenModal}>생성하기</button>
              </div>
            </section>
          )}
          {status === "IN_PROGRESS" && (
            <section className="coding-test-start">
              <h2>진행 중인 코딩 테스트가 있습니다.</h2>
              <div className="coding-test-start-question">
                <h4>입장하시겠습니까?</h4>
                <button onClick={handleEnter}>입장하기</button>
              </div>
            </section>
          )}
          {status === "SUBMITTED" && (
            <section className="coding-test-start">
              <h2>코딩 테스트를 제출하셨습니다.</h2>
              <div className="coding-test-start-question">
                <h4>코딩테스트가 아직 진행 중입니다...</h4>
                <h4>수고하셨습니다.</h4>
              </div>
            </section>
          )}
          {status === "FINISHED" && (
            <section className="coding-test-start">
              <h2>코딩 테스트가 끝났습니다.</h2>
              <div className="coding-test-start-question">
                <h4>수고하셨습니다.</h4>
              </div>
            </section>
          )}
        </>
      )}
    </>
  );
}

export default CodingTestStart;
