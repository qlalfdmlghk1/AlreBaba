import React from "react";
import CodingTest from "../components/CodingTest";
import { useOutletContext, useParams } from "react-router-dom";

function ProblemDetailPage() {
  const { channelId } = useParams();
  const { problems, activeProblem, setActiveProblem } = useOutletContext();
  sessionStorage.setItem(`channelId-${channelId}-boolean`, true);

  return (
    <div>
      <CodingTest
        problems={problems}
        activeProblem={activeProblem}
        setActiveProblem={setActiveProblem}
      />
    </div>
  );
}

export default ProblemDetailPage;
