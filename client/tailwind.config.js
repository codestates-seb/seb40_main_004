/** @type {import('tailwindcss').Config} */
module.exports = {
  // 어디서 사용할지
  // pages, components 디렉토리(**)의 모든파일(*) 해당확장자
  content: [
    './pages/**/*.{js,jsx,ts,tsx}',
    './components/**/*.{js,jsx,ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        'main-orange': '#FF9F10',
        'main-yellow': '#FFDF6B',
        'font-gray': '#3A3A3A',
        'main-gray': '#A0A0A0',
        'background-gray': '#F2F2F2',
      },
    },
  },
  plugins: [require('@tailwindcss/typography')],
};
