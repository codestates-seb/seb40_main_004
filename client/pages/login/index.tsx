import { GetServerSideProps, NextPage } from 'next';

import { Footer } from '@components/common/Footer';
import { Header } from '@components/common/Header';
import { Seo } from '@components/common/Seo';
import { LoginForm } from '@components/login/LoginForm';

const Login: NextPage = () => {
  return (
    <div className="h-screen">
      <Seo title="로그인" />
      <Header />
      <main className="flex flex-col justify-center items-center h-[79vh] bg-white">
        <article className="text-center">
          <h3 className="font-bold text-2xl">로그인</h3>
          <h3 className="mt-4 font-semibold">다시 오셨군요! 환영합니다.</h3>
          <LoginForm />
        </article>
      </main>
      <Footer />
    </div>
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const content = context.req.url?.split('/')[1];
  return {
    props: {
      content,
    },
  };
};

export default Login;
