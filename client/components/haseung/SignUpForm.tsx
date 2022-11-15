/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-15
 * 개요: 가입하기 버튼에 인증번호 발송 페이지로 가는 링크 추가
 */

import Link from 'next/link';
import { useForm } from 'react-hook-form';

import { Divider } from './Divider';
import { SocialLoginBtn } from './SocialLoginBtn';

type SignUpProps = {
  email: string;
  password: string;
  confirmPassword: string;
};

export const SignUpForm = () => {
  const onValid = ({ email, password, confirmPassword }: SignUpProps) => {
    console.log(email, password, confirmPassword);
  };
  const { register, handleSubmit } = useForm<SignUpProps>();
  return (
    <form
      className="flex flex-col mx-auto justify-center items-start mt-10"
      onSubmit={handleSubmit(onValid)}
    >
      <label>이메일</label>
      <input
        {...register('email', { required: true })}
        className="rounded-full w-96 h-10 placeholder:text-base placeholder:pl-3 placeholder:pb-2"
        type="text"
        placeholder="hyejj19@naver.com"
      />
      <label>비밀번호</label>
      <input
        {...register('password', { required: true })}
        className="rounded-full w-96 h-10"
        type="password"
        autoComplete="off"
      />
      <label>비밀번호 확인</label>
      <input
        {...register('confirmPassword', { required: true })}
        className="rounded-full w-96 h-10"
        type="password"
        autoComplete="off"
      />
      <Link href="/authenticationNumber">
        <button
          className="mx-auto mt-4 bg-main-yellow rounded-full w-96 h-10 hover:bg-main-orange"
          type="submit"
        >
          가입하기
        </button>
      </Link>
      <span className="mt-3 text-main-gray">
        이미 계정이 있으신가요?{' '}
        <Link href="/login">
          <span className="text-blue-500 cursor-pointer hover:text-blue-400">
            로그인
          </span>
        </Link>
      </span>
      <Divider />
      <SocialLoginBtn />
    </form>
  );
};
