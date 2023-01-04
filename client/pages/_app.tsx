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
config.autoAddCss = false;

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
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
