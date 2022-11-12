/** @type {import('tailwindcss').Config} */
module.exports = {
  // 어디서 사용할지
  // pages, components 디렉토리(**)의 모든파일(*) 해당확장자
  content: [
    "./pages/**/*.{js,jsx,ts,tsx}",
    "./components/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};
