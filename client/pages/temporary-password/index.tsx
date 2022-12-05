import axios from 'axios';
import { useRouter } from 'next/router';
import React from 'react';
import { useForm } from 'react-hook-form';
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import { userEmailAtom } from '../../atomsHS';

type TemporaryPasswordProps = {
  email: string;
  authKey: string;
};

const TemporaryPassword = () => {
  const { register, handleSubmit } = useForm<TemporaryPasswordProps>({
    mode: 'onChange',
  });
  const [email, setEmail] = useRecoilState(userEmailAtom);
  const router = useRouter();
  const onValid = ({ email, authKey }: TemporaryPasswordProps) => {
    axios
      .post(`/api/auth/password/recovery`, { email, authKey })
      .then((res) => {
        setEmail(email);
        console.log('res2', res);
        alert('임시 비밀번호가 발급되었습니다! 메일을 확인해주세요.');
      })
      .catch((error) => console.error('error', error));

    // router.push('/signup');
  };
  return (
    <form onSubmit={handleSubmit(onValid)} className="w-full">
      <label className="font-bold flex-col flex mx-2">이메일</label>
      <input
        {...register('email', { required: true })}
        className="rounded-full w-96 h-10 
        pl-4
        border
        mb-5
        mx-2
        "
        type="text"
        placeholder="이메일을 입력해주세요."
      />
      <label className="font-bold flex-col flex mx-2">인증번호</label>
      <input
        {...register('authKey', { required: true })}
        type="text"
        placeholder="인증번호를 입력하세요."
        className="rounded-full w-full h-10 pl-4 border my-5 mx-2"
      />
      <button className="bg-main-yellow py-3 w-full rounded-[20px] font-bold mb-5">
        인증하기
      </button>
    </form>
  );
};

export default TemporaryPassword;
