import axios from 'axios';
import { useRouter } from 'next/router';
import React from 'react';
import { useForm } from 'react-hook-form';
import { useSetRecoilState } from 'recoil';
import { userEmailAtom } from '../../../atomsHS';
import { Footer } from '../../../components/common/Footer';
import { Header } from '../../../components/common/Header';

type CheckAuthCodeProps = {
  email: string;
  authKey: string;
};

const CheckAuthCode = () => {
  const { register, handleSubmit } = useForm<CheckAuthCodeProps>({
    mode: 'onChange',
  });
  const setEmail = useSetRecoilState(userEmailAtom);
  const router = useRouter();
  const onValid = ({ email, authKey }: CheckAuthCodeProps) => {
    axios
      .post(`/api/auth/password/recovery`, { email, authKey })
      .then(() => {
        setEmail(email);
        alert('임시 비밀번호가 발급되었습니다! 메일을 확인해주세요.');
        router.push('/login');
      })
      .catch((error) => console.error('error', error));
  };
  return (
    <>
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
        </form>
      </main>
      <Footer />
    </>
  );
};

export default CheckAuthCode;
