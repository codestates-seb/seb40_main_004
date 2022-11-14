import '../styles/globals.css';
import type { AppProps } from 'next/app';
import 'react-quill/dist/quill.snow.css';
import { RecoilRoot } from 'recoil';
import Head from 'next/head';

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <>
      <RecoilRoot>
        <div className="bg-background-gray text-font-gray min-h-screen">
          <Component {...pageProps} />
        </div>
      </RecoilRoot>
    </>
  );
}

export default MyApp;
