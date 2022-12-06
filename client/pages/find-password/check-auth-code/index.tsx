import axios from 'axios';
import { GetServerSideProps, NextPage } from 'next';
import Head from 'next/head';
import { useRouter } from 'next/router';
import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useSetRecoilState } from 'recoil';
import { userEmailAtom } from '../../../atomsHS';
import { Footer } from '../../../components/common/Footer';
import { Header } from '../../../components/common/Header';
import { Loader } from '../../../components/common/Loader';
import { AuthProps, TitleProps } from '../../../libs/interfaces';

const CheckAuthCode: NextPage<TitleProps> = ({
  title = '임시 비밀번호 발급',
}) => {
  const { register, handleSubmit } = useForm<AuthProps>({
    mode: 'onChange',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const setEmail = useSetRecoilState(userEmailAtom);
  const router = useRouter();
  const onValid = ({ email, authKey }: AuthProps) => {
    axios
      .post(`/api/auth/password/recovery`, { email, authKey })
      .then(() => {
        setIsSubmitting(true);
        setEmail(email);
        alert('임시 비밀번호가 발급되었습니다! 메일을 확인해주세요.');
        router.push('/login');
      })
      .catch((error) => console.error('error', error));
  };
  return (
    <>
      <Head>
        <title>{title}</title>
      </Head>
      <Header />
      <main className="flex flex-col justify-center items-center h-[79vh] bg-white">
        <form
          onSubmit={handleSubmit(onValid)}
          className="flex flex-col mx-auto justify-center items-start mt-10 space-y-2"
        >
          <label className="font-bold flex-col flex mx-2">이메일</label>
          <input
            {...register('email', { required: true })}
            className="rounded-full w-96 h-10 pl-4 border"
            type="text"
            placeholder="이메일을 입력해주세요."
          />
          <label className="font-bold flex-col flex mx-2">인증번호</label>
          <input
            {...register('authKey', { required: true })}
            type="text"
            placeholder="인증번호를 입력하세요."
            className="rounded-full w-96 h-10 pl-4 border"
          />
          <button className="bg-main-yellow bg-opacity-80 py-3 w-full rounded-[20px] font-bold mb-5 hover:bg-main-yellow">
            임시 비밀번호 발급
          </button>
          <p className="text-center relative top-20 font-bold text-xl">
            {isSubmitting ? (
              <>
                <Loader /> <span>임시 비밀번호 전송 중....</span>
              </>
            ) : null}
          </p>
        </form>
      </main>
      <Footer />
    </>
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

export default CheckAuthCode;
