import { NavLink } from "react-router-dom";
import "./CodingTestNavigation.css";

function CodingTestNavigation({ data, onClick }) {
  function handleLinkClick(problemId, problemUrl) {
    onClick(problemId);
    window.open(problemUrl);
  }
  return (
    <>
      {!data && (
        <p className="coding-test-nav-loading">문제 목록을 로딩 중입니다...</p>
      )}
      {data && (
        <div className="problem-list-container">
          <h2>문제 목록</h2>
          <ul className="coding-test-navigation">
            {data.map((item) => (
              <li
                key={item.problemId}
                onClick={() =>
                  handleLinkClick(item.problemId, item.problemUrl)
                }>
                <NavLink
                  to={`${item.problemId}`}
                  className={({ isActive }) =>
                    isActive ? "active" : undefined
                  }>
                  {item.problemTitle}
                </NavLink>
              </li>
            ))}
            <li>
              <p>문제를 클릭하면 입력하신 문제를 보실 수 있습니다.</p>
            </li>
          </ul>
          <ul></ul>
        </div>
      )}
    </>
  );
}

export default CodingTestNavigation;
