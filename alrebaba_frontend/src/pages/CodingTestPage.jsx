import ChannelStudyBar from "../components/LeftSection/ChannelStudyBar";
import TitleBar from "../components/TitleBar";
import InnerFooter from "../components/InnerFooter";
import InnerHeaderBase from "../components/InnerHeader/InnerHeaderBase";
import CodingTestNavigation from "../components/CodingTestNavigation";
import { Outlet, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { getCTProblems } from "../service/codingTest";
import "./CodingTestPage.css";

function CodingTestPage() {
  const { problemId, channelId } = useParams();
  const codingTestId = sessionStorage.getItem(`channelId-${channelId}`);
  const [isError, setIsError] = useState(false);
  const [problems, setProblems] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [activeProblem, setActiveProblem] = useState(Number(problemId));

  useEffect(() => {
    async function fetchData() {
      setIsLoading(true);

      try {
        const response = await getCTProblems(codingTestId);
        setProblems(response.data);
        setIsLoading(false);
      } catch (error) {
        setIsLoading(false);
        setIsError(true);
      }
    }
    fetchData();
  }, [codingTestId]);

  return (
    <div className="layout-container">
      <div className="left ct">
        <ChannelStudyBar />
        {/* <ChannelUserBar /> */}
      </div>
      <div className="center ct">
        <TitleBar identifier={"코딩테스트"} />
        <InnerHeaderBase />
        <div className="ct-nav-container">
          <span className="ct-nav">
            <CodingTestNavigation data={problems} onClick={setActiveProblem} />
          </span>
          <span className="problem-container">
            <Outlet context={{ problems, activeProblem, setActiveProblem }} />
          </span>
        </div>
        <InnerFooter />
      </div>
    </div>
  );
}

export default CodingTestPage;
