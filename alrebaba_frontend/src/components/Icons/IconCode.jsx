function IconCode({ color = "#80848E" }) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="100%"
      height="100%"
      viewBox="0 0 12 9"
      fill="none"
    >
      <g clipPath="url(#clip0_137_974)">
        <path
          d="M3.42859 8L0.428589 4.5L3.42859 1"
          stroke={color}
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M8.57141 8L11.5714 4.5L8.57141 1"
          stroke={color}
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </g>
      <defs>
        <clipPath id="clip0_137_974">
          <rect width="12" height="9" fill="white" />
        </clipPath>
      </defs>
    </svg>
  );
}

export default IconCode;
