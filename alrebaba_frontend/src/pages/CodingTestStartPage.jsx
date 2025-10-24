import CodingTestStart from "../components/CodingTestStart";
import ChannelStudyBar from "../components/LeftSection/ChannelStudyBar";
import TitleBar from "../components/TitleBar";
import InnerFooter from "../components/InnerFooter";
import InnerHeaderBase from "../components/InnerHeader/InnerHeaderBase";
import ChannelUserBar from "../components/LeftSection/ChannelUserBar";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { getCTInfo, getCTProblems } from "../service/codingTest";
import { studyDetail } from "../service/study";

function CodingTestStartPage({ channelName }) {
  const { studyId, channelId } = useParams();
  const [status, setStatus] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  // useEffect(() => {
  //   async function fetchCTId() {
  //     try {
  //       const response = await getCTInfo({ channelId });

  //       // 이미 생성된 코딩테스트
  //       if (response.data.length !== 0) {
  //         const currentTime = new Date();
  //         const endTime = new Date(response.data.endTime);

  //         // 타이머 시각이 이미 종료되었다면
  //         if (currentTime.getTime() > endTime.getTime()) {
  //           setStatus("FINISHED");
  //         } else {
  //           const codingTestId = sessionStorage.getItem(
  //             `channelId-${channelId}`
  //           );

  //           // 코딩테스트 아이디 존재 - 사용자가 임의로 방을 나갔다 들어옴
  //           if (codingTestId) {
  //             // const response2 = await getCTProblems(codingTestId);

  //             // navigate(`${codingTestId}/${response2.data[0].problemId}`, {
  //             //   replace: true,
  //             // });
  //             setStatus("IN_PROGRESS");
  //           } else {
  //             // 코딩텥스트 아이디 존재하지 않음
  //             // 근데 만약 다른 스터디 참여자라면?
  //             // 채널을 만든 사람과 현재 로그인한 사용자 비교
  //             const response2 = await studyDetail(studyId);
  //             const createdBy = response2.data.createdBy;
  //             const currentUser = sessionStorage.getItem("userData.memberId");
  //             if (Number(createdBy) !== Number(currentUser)) {
  //               setStatus("IN_PROGRESS");
  //             } else {
  //               setStatus("FINISHED");
  //             }
  //           }
  //         }
  //       } else {
  //         // 아직 만들어지지 않은 코딩테스트
  //         setStatus("NOT_YET");
  //       }
  //     } catch (error) {
  //       alert("데이터 불러오기 실패:", error);
  //     }
  //   }
  //   fetchCTId();
  // }, [channelId, navigate]);

  // 타이머가 종료되기 전에 유저가 코테를 종료할 수 있어서 이렇게 처리합니다.
  useEffect(() => {
    async function fetchCTId() {
      setIsLoading(true);
      try {
        const response = await getCTInfo({ channelId });
        // console.log("ALEAADY CREATED?", response);

        // 이미 생성된 코딩테스트가 있을 때
        if (response.data.length !== 0) {
          const currentTime = new Date();
          const endTime = new Date(response.data[0].endTime);

          const currentTimeUTC = new Date(currentTime.toISOString());
          const endTimeUTC = new Date(endTime.toISOString());

          // 타이머 시각이 이미 종료되었다면
          if (currentTimeUTC.getTime() > endTimeUTC.getTime()) {
            setStatus("FINISHED");
          } else {
            const codingTestId = sessionStorage.getItem(
              `channelId-${channelId}`
            );

            // 코딩테스트 아이디가 존재할 때
            // 사용자가 임의로 방을 나갔다 들어옴
            if (codingTestId) {
              try {
                const response2 = await getCTProblems(codingTestId);

                // 문제 리스트가 존재하면 진행 중
                if (response2.data.length > 0) {
                  setStatus("IN_PROGRESS");
                } else {
                  setStatus("NOT_YET");
                }
              } catch (error) {
                console.error("문제 불러오기 실패:", error);
                setStatus("NOT_YET");
              }
            } else {
              // 코딩테스트 아이디가 존재하지 않음
              const response2 = await studyDetail(studyId);
              const createdBy = response2.data.createdBy;
              const currentUser = sessionStorage.getItem("userData.memberId");

              if (Number(createdBy) !== Number(currentUser)) {
                // 방 생성자가 아닌 사용자의 경우
                try {
                  // 문제 리스트가 존재하면 진행 중, 아니면 참여하지 않음
                  const response3 = await getCTProblems(
                    response.data.codingTestId
                  );
                  const isProgress = sessionStorage.getItem(
                    `channelId-${channelId}-boolean`
                  );
                  if (isProgress === "false") {
                    setStatus("SUBMITTED");
                  } else {
                    if (response3.data.length > 0) {
                      setStatus("IN_PROGRESS");
                    } else {
                      setStatus("NOT_YET");
                    }
                  }
                } catch (error) {
                  console.error("문제 불러오기 실패:", error);
                  setStatus("NOT_YET");
                }
              } else {
                // 방 생성자인 경우
                setStatus("SUBMITTED");
              }
            }
          }
        } else {
          // 아직 만들어지지 않은 코딩테스트
          setStatus("NOT_YET");
        }
      } catch (error) {
        alert("데이터 불러오기 실패:", error);
      } finally {
        setIsLoading(false);
      }
    }
    fetchCTId();
  }, [channelId, studyId, navigate]);

  return (
    <>
      <div className="layout-container">
        <div className="left">
          <ChannelStudyBar />
          <ChannelUserBar />
        </div>
        <div className="center">
          <TitleBar identifier={"코딩 테스트"} channelName={channelName} />
          <InnerHeaderBase />
          <CodingTestStart status={status} isLoading={isLoading} />
          <InnerFooter />
        </div>
      </div>
    </>
  );
}

export default CodingTestStartPage;
