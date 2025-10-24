import { Editor } from "@monaco-editor/react";
import { useEffect, useRef, useState } from "react";
import "./CodingTest.css";
import ProgressBar from "./ProgressBar";
import IconDarkMode from "./Icons/IconDarkMode";
import IconLightMode from "./Icons/IconLightMode";
import ModalCloseCodingTest from "../components/Modals/ModalCloseCodingTest";
import {
  getCodeDetail,
  postCTCode,
  patchCTCode,
  executeCode,
  getCode,
} from "../service/codingTestCode";
import { useNavigate, useParams } from "react-router-dom";

export const CODE_SNIPPETS = {
  javascript: `\nfunction greet(name) {\n\tconsole.log("Hello, " + name + "!");\n}\n\ngreet("Alex");\n`,
  Python: `\ndef greet(name):\n\tprint("Hello, " + name + "!")\n\ngreet("Alex")\n`,
  Java: `\npublic class HelloWorld {\n\tpublic static void main(String[] args) {\n\t\tSystem.out.println("Hello World");\n\t}\n}\n`,
  "C#": `using System;\n\nnamespace HelloWorld\n{\n\tclass Hello { \n\t\tstatic void Main(string[] args) {\n\t\t\tConsole.WriteLine("Hello World in C#");\n\t\t}\n\t}\n}\n`,
  php: "<?php\n\n$name = 'Alex';\necho $name;\n",
  "node.js": `\nconsole.log("Hello, Node.js!");\n`,
  C: `#include <stdio.h>\n\nint main() {\n    printf("Hello, C!\\n");\n    return 0;\n}\n`,
  "C++": `#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << "Hello, C++!" << endl;\n    return 0;\n}\n`,
  Rust: `fn main() {\n    println!("Hello, Rust!");\n}\n`,
  Ruby: `\nname = "Alex"\nputs "Hello, #{name}!"\n`,
};

function CodingTest({ problems, activeProblem, setActiveProblem }) {
  const navigate = useNavigate();
  const { studyId, channelId, problemId, testId } = useParams();

  const [language, setLanguage] = useState("Python");
  const [mode, setMode] = useState("vs-dark");
  const [code, setCode] = useState(CODE_SNIPPETS[language]);
  const [isSubmitButtonClicked, setIsButtonClicked] = useState(false);
  const [isFetching, setIsFetching] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [result, setResult] = useState(
    "시작하려면 입력란에 코드를 작성하고 실행 버튼을 눌러 결과를 확인하세요."
  );
  const [isError, setIsError] = useState(false);
  const [codeId, setCodeId] = useState(null);

  const editorRef = useRef(null);

  // 활성화된 문제가 변경될 때 해당 문제에 대한 코드 로드
  useEffect(() => {
    const savedCodeId = sessionStorage.getItem(`codeId-${activeProblem}`);
    setCodeId(savedCodeId);

    if (!savedCodeId) {
      // 저장된 코드가 없으면 현재 언어의 기본 스니펫 사용
      setCode(CODE_SNIPPETS[language]);
    } else {
      // 저장된 코드가 있으면 가져오기
      async function fetchCodeDetail() {
        try {
          const response = await getCodeDetail(savedCodeId);
          const savedLanguage = response.data.language || "Python";
          setLanguage(savedLanguage);
          setCode(response.data.context.replace(/\\n/g, "\n"));
        } catch (error) {
          console.error("코드 로드 중 오류:", error);
          setCode(CODE_SNIPPETS[language]);
        }
      }
      fetchCodeDetail();
    }
  }, [activeProblem]);

  // 모드 변경
  function handleModeChange() {
    setMode((prev) => (prev === "vs-dark" ? "vs-light" : "vs-dark"));
  }

  // 언어 변경 - 수정된 함수
  function handleLanguageChange(event) {
    const newLanguage = event.target.value;
    setLanguage(newLanguage);

    const savedCodeId = sessionStorage.getItem(`codeId-${activeProblem}`);

    if (!savedCodeId) {
      // 저장된 코드가 없으면 바로 새 언어의 기본 스니펫 설정
      setCode(CODE_SNIPPETS[newLanguage]);
    } else {
      // 저장된 코드가 있으면 해당 언어의 코드 가져오기 시도
      async function loadLanguageCode() {
        try {
          // 명시적으로 새 언어를 파라미터로 전달
          const response = await getCode({
            codeId: savedCodeId,
            language: newLanguage,
          });

          if (response.data.content && response.data.content.length > 0) {
            // 해당 언어로 저장된 코드가 있을 때
            const detailResponse = await getCodeDetail(savedCodeId);
            setCode(detailResponse.data.context.replace(/\\n/g, "\n"));
          } else {
            // 해당 언어로 저장된 코드가 없을 때 기본 스니펫 사용
            setCode(CODE_SNIPPETS[newLanguage]);
          }
        } catch (error) {
          console.error("언어 변경 중 코드 로드 오류:", error);
          setCode(CODE_SNIPPETS[newLanguage]);
        }
      }

      loadLanguageCode();
    }
  }

  // 코드 변경 감지
  const handleEditorChange = (value) => {
    setCode(value);
  };

  // 다음 문제로 이동
  async function handleNextButtonClick() {
    const nextProblemId = Number(problemId) + 1;
    const currentProblem = problems.find(
      (problem) => problem.problemId === activeProblem
    );

    if (currentProblem) {
      setIsSending(true);

      try {
        // codeId가 존재하면 patch 요청, 없으면 post 요청
        if (codeId) {
          await patchCTCode({
            codeId: codeId,
            platform: "BOJ",
            title: currentProblem.problemTitle,
            context: code,
            language: language,
          });
          sessionStorage.setItem(`codeId-${problemId}`, codeId);
        } else {
          const response = await postCTCode(channelId, {
            platform: "BOJ",
            title: currentProblem.problemTitle,
            context: code,
            language: language,
            problemId: Number(activeProblem),
          });
          const newCodeId = response.data.codeId;
          sessionStorage.setItem(`codeId-${problemId}`, newCodeId);
          setCodeId(newCodeId);
        }

        setIsSending(false);
        setActiveProblem(nextProblemId);
        navigate(`/study/${studyId}/${channelId}/${testId}/${nextProblemId}`);
      } catch (error) {
        console.error("다음 문제 이동 중 오류:", error);
        setIsSending(false);
      }
    }
  }

  async function handleSubmit() {
    setIsButtonClicked(true);

    try {
      if (codeId) {
        await patchCTCode({
          codeId: codeId,
          platform: "BOJ",
          title: currentProblem.problemTitle,
          context: code,
          language: language,
        });
        sessionStorage.setItem(`codeId-${problemId}`, codeId);
      } else {
        const response = await postCTCode(channelId, {
          platform: "BOJ",
          title: currentProblem.problemTitle,
          context: code,
          language: language,
          problemId: Number(activeProblem),
        });
        const newCodeId = response.data.codeId;
        sessionStorage.setItem(`codeId-${problemId}`, newCodeId);
        setCodeId(newCodeId);
      }
    } catch (error) {
      console.error("코드 제출 중 오류:", error);
    }
  }

  // 코드 실행
  // 코드 실행 및 저장
  async function runCode() {
    const sourceCode = code;
    if (!sourceCode) return;

    setIsFetching(true);

    try {
      const currentProblem = problems.find(
        (problem) => problem.problemId === activeProblem
      );

      if (currentProblem) {
        if (codeId) {
          await patchCTCode({
            codeId: codeId,
            platform: "BOJ",
            title: currentProblem.problemTitle,
            context: code,
            language: language,
          });
        } else {
          const response = await postCTCode(channelId, {
            platform: "BOJ",
            title: currentProblem.problemTitle,
            context: code,
            language: language,
            problemId: Number(activeProblem),
          });
          const newCodeId = response.data.codeId;
          sessionStorage.setItem(`codeId-${problemId}`, newCodeId);
          setCodeId(newCodeId);
        }
      }

      const data = await executeCode(language, code);
      setResult(data.run.output.split("\n"));
      data.run.stderr ? setIsError(true) : setIsError(false);
    } catch (error) {
      console.error("코드 실행 또는 저장 중 오류:", error);
      setResult(["실행 또는 저장 중 오류가 발생했습니다."]);
      setIsError(true);
    }

    setIsFetching(false);
  }

  const currentProblem = problems?.find(
    (problem) => problem.problemId === activeProblem
  );

  return (
    <>
      {isSubmitButtonClicked && (
        <ModalCloseCodingTest
          open={isSubmitButtonClicked}
          onClose={() => setIsButtonClicked(false)}
          code={code}
          activeProblem={activeProblem}
        />
      )}
      <section className="coding-test-main">
        <span className="right">
          <div className="ct-info">
            <span className="ct-info-lang-mode">
              <select
                id="language"
                value={language}
                onChange={handleLanguageChange}
              >
                <option value="Python">Python</option>
                <option value="node.js">JavaScript</option>
                <option value="Java">Java</option>
                <option value="C#">C#</option>
                <option value="C">C</option>
                <option value="C++">C++</option>
                <option value="Rust">Rust</option>
                <option value="Ruby">Ruby</option>
              </select>
              <button onClick={handleModeChange}>
                {mode === "vs-dark" ? <IconLightMode /> : <IconDarkMode />}
              </button>
            </span>
            <div className="progress-bar-container">
              <ProgressBar timer={100} />
            </div>
          </div>
          <div className="coding-test-editor">
            <Editor
              theme={mode}
              language={
                language.toLowerCase() === "node.js"
                  ? "javascript"
                  : language.toLowerCase()
              }
              value={code}
              onChange={handleEditorChange}
              onMount={(editor) => (editorRef.current = editor)}
              options={{
                selectOnLineNumbers: true,
                fontSize: 14,
                minimap: { enabled: false },
              }}
              wrapperClassName="editor-wrapper"
              width={"60%"}
              height={"100%"}
            />
            <span className="console-container">
              <h2>Console</h2>
              <div className="console">
                <span className={`${isError ? "error" : ""}`}>
                  {isFetching && <p>실행 중입니다....</p>}
                  {!isFetching && (
                    <ul>
                      {Array.isArray(result) ? (
                        result.map((item, index) => <li key={index}>{item}</li>)
                      ) : (
                        <li>{result}</li>
                      )}
                    </ul>
                  )}
                </span>
              </div>
            </span>
          </div>
          <div className="ct-button-actions">
            <button className="execute" onClick={runCode}>
              실행하기
            </button>
            {problems &&
              activeProblem !== problems[problems.length - 1]?.problemId && (
                <button
                  className="save"
                  onClick={handleNextButtonClick}
                  disabled={isSending}
                >
                  {isSending ? "저장 중입니다..." : "다음으로"}
                </button>
              )}
            {problems &&
              activeProblem === problems[problems.length - 1]?.problemId && (
                <button className="save" onClick={handleSubmit}>
                  제출하기
                </button>
              )}
          </div>
        </span>
      </section>
    </>
  );
}

export default CodingTest;
