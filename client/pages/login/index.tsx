/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-01 (박혜정)
 * 개요: 로그인에 대한 form을 표시합니다.
 */

import { LoginForm } from '../../components/haseung/LoginForm';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { GetServerSideProps, NextPage } from 'next';
import Head from 'next/head';
import { TitleProps } from '../../libs/interfaces';

const Login: NextPage<TitleProps> = ({ title = '로그인' }) => {
  return (
    <div className="h-screen">
      <Head>
        <title>{title}</title>
      </Head>
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
