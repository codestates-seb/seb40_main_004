/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-01(박혜정)
 * 개요: 회원가입에 대한 form을 표시합니다.
 */

import { Intro } from '../../components/haseung/Intro';
import { SignUpForm } from '../../components/haseung/SignUpForm';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { GetServerSideProps, NextPage } from 'next';
import Head from 'next/head';
import { TitleProps } from '../../libs/interfaces';

const SignUp: NextPage<TitleProps> = ({ title = '회원가입' }) => {
  return (
    <div className="h-screen">
      <Head>
        <title>{title}</title>
      </Head>
      <Header />
      <main className="flex flex-col justify-center items-center h-[90%] bg-white">
        <article className="text-center">
          <Intro />
          <SignUpForm />
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

export default SignUp;
