/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-15
 */

import Link from 'next/link';
import { useForm } from 'react-hook-form';

type LoginProps = {
  email: string;
  password: string;
};

export const LoginForm = () => {
  const onValid = ({ email, password }: LoginProps) => {
    console.log(email, password);
  };
  const { register, handleSubmit } = useForm<LoginProps>();
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
      <Link href="/edit-password">
        <span className="text-xs mt-3 cursor-pointer hover:text-main-gray">
          비밀번호를 잊어버리셨나요?
        </span>
      </Link>
      <span className="text-xs mt-3 cursor-pointer text-main-gray">
        계정이 없으신가요?{' '}
        <Link href="/signup">
          <span className="text-blue-500 hover:text-blue-400">가입</span>
        </Link>
      </span>
      <button
        className="mx-auto mt-4 bg-main-yellow rounded-full w-96 h-10 hover:bg-main-orange"
        type="submit"
      >
        로그인
      </button>
    </form>
  );
};
