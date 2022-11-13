import '../styles/globals.css';
import { AppProps } from 'next/app';
import { RecoilRoot } from 'recoil';

function MyApp({ Component, pageProps }: AppProps) {
  return (
    <RecoilRoot>
      <div className="bg-background-gray text-font-gray min-h-screen">
        <Component {...pageProps} />
      </div>
    </RecoilRoot>
  );
}

export default MyApp;
