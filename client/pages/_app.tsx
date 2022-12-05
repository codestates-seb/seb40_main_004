import type { AppProps } from 'next/app';
import { RecoilRoot } from 'recoil';
import '../styles/globals.css';
import 'react-quill/dist/quill.snow.css';
import '@fullcalendar/common/main.css';
import '@fullcalendar/daygrid/main.css';
import Head from 'next/head';

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <RecoilRoot>
        <div className="bg-background-gray text-font-gray min-h-screen">
          <Head>
            <title>모락모락</title>
          </Head>
          <Component {...pageProps} />
        </div>
      </RecoilRoot>
    </>
  );
}

export default MyApp;
