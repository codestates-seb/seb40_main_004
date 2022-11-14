/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

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
        className="rounded-full w-96 h-10 border border-font-gray placeholder:text-base placeholder:pl-3 placeholder:pb-2"
        type="text"
        placeholder="hyejj19@naver.com"
      />
      <label>비밀번호</label>
      <input
        {...register('password', { required: true })}
        className="rounded-full border w-96 h-10 border-font-gray"
        type="password"
        autoComplete="off"
      />
      <label>비밀번호 확인</label>
      <input
        {...register('confirmPassword', { required: true })}
        className="rounded-full border w-96 h-10 border-font-gray"
        type="password"
        autoComplete="off"
      />
      <button
        className="mx-auto mt-4 bg-main-yellow rounded-full w-96 h-10 hover:bg-main-orange"
        type="submit"
      >
        가입하기
      </button>
      <Divider />
      <SocialLoginBtn />
    </form>
  );
};
