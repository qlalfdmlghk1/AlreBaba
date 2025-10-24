function IconFullScreen({ size = "100%" }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 20 20"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M5 0.5H1C0.867392 0.5 0.740215 0.552678 0.646447 0.646447C0.552678 0.740215 0.5 0.867392 0.5 1V5"
        stroke="white"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M13.5 5V1C13.5 0.867392 13.4473 0.740215 13.3536 0.646447C13.2598 0.552678 13.1326 0.5 13 0.5H9"
        stroke="white"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M9 13.5H13C13.1326 13.5 13.2598 13.4473 13.3536 13.3536C13.4473 13.2598 13.5 13.1326 13.5 13V9"
        stroke="white"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M0.5 9V13C0.5 13.1326 0.552678 13.2598 0.646447 13.3536C0.740215 13.4473 0.867392 13.5 1 13.5H5"
        stroke="white"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

export default IconFullScreen;
