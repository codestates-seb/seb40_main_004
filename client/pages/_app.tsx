import type { AppProps } from 'next/app';
import { RecoilRoot } from 'recoil';
import '../styles/globals.css';
import 'react-quill/dist/quill.snow.css';
import '@fullcalendar/common/main.css';
import '@fullcalendar/daygrid/main.css';
// fontAwesome 미리 렌더링 방지
import '@fortawesome/fontawesome-svg-core/styles.css';
import { config } from '@fortawesome/fontawesome-svg-core';
import 'react-confirm-alert/src/react-confirm-alert.css'; //react-confirm-alert 사용을 위한 css import
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; //react-toastify 사용을 위한 css import
import Head from 'next/head';
config.autoAddCss = false;

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        {/* HTML Meta Tags */}
        <title>모락모락</title>
        <meta
          name="description"
          content="당신 가까이, 가장 따뜻한 개발자 커뮤니티 모락모락"
        ></meta>

        {/* Facebook Meta Tags  */}
        <meta
          property="og:url"
          content="https://seb40-main-004.vercel.app/"
        ></meta>
        <meta property="og:type" content="website"></meta>
        <meta property="og:title" content="모락모락"></meta>
        <meta
          property="og:description"
          content="당신 가까이, 가장 따뜻한 개발자 커뮤니티 모락모락"
        ></meta>
        <meta property="og:image" content="../public/morak-main-1.png"></meta>

        {/* Twitter Meta Tags  */}
        <meta name="twitter:card" content="../public/morak-main-1.png"></meta>
        <meta
          property="twitter:domain"
          content="seb40-main-004.vercel.app"
        ></meta>
        <meta
          property="twitter:url"
          content="https://seb40-main-004.vercel.app/"
        ></meta>
        <meta name="twitter:title" content="모락모락"></meta>
        <meta
          name="twitter:description"
          content="당신 가까이, 가장 따뜻한 개발자 커뮤니티 모락모락"
        ></meta>
        <meta name="twitter:image" content="../public/morak-main-1.png"></meta>
      </Head>
      <RecoilRoot>
        <div className="bg-background-gray text-font-gray min-h-screen">
          <Component {...pageProps} />
          <ToastContainer
            theme="colored"
            autoClose={3000}
            position="top-center"
          />
        </div>
      </RecoilRoot>
    </>
  );
}

export default MyApp;
