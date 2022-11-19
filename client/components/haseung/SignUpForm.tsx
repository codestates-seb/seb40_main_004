/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-20
 * 개요: 가입하기 버튼에 인증번호 발송 페이지로 가는 링크 추가
 */

import axios from 'axios';
import Link from 'next/link';
import { SubmitHandler, useForm } from 'react-hook-form';
import { Button } from '../common/Button';
import { Divider } from './Divider';
import { SocialLoginBtn } from './SocialLoginBtn';

type SignUpProps = {
  email: string;
  password: string;
  confirmPassword: string;
  nickName: string;
};

export const SignUpForm = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<SignUpProps>();

  const onValid: SubmitHandler<SignUpProps> = ({
    email,
    password,
    confirmPassword,
    nickName,
  }) => {
    axios
      .post('http://localhost:3000/users', {
        email,
        password,
        confirmPassword,
        nickName,
      })
      .then((res) => res)
      .catch((error) => {
        console.log(error);
      });
  };

  return (
    <form
      className="flex flex-col mx-auto justify-center items-start mt-3 space-y-5"
      onSubmit={handleSubmit(onValid)}
    >
      <label htmlFor="nickName" className="font-bold">
        닉네임
      </label>
      <input
        {...register('nickName', { required: true })}
        className="rounded-full w-96 h-10 placeholder:text-base placeholder:pl-3 placeholder:pb-2"
        type="text"
        placeholder="닉네임을 입력해주세요."
      />
      {errors.nickName?.message ? '닉네임을 입력해주세요.' : ''}
      <label htmlFor="email" className="font-bold">
        이메일
      </label>
      <input
        {...register('email', { required: true })}
        className="rounded-full w-96 h-10 placeholder:text-base placeholder:pl-3 placeholder:pb-2"
        type="text"
        placeholder="이메일을 입력해주세요."
      />
      {errors.email?.message ? '이메일은 필수 항목입니다.' : ''}
      <label htmlFor="password" className="font-bold">
        비밀번호
      </label>
      <input
        {...register('password', { required: true })}
        className="rounded-full w-96 h-10"
        type="password"
        autoComplete="off"
      />
      {errors.password?.message ? '비밀번호를 확인해주세요.' : ''}
      <label htmlFor="confirmPassword" className="font-bold">
        비밀번호 확인
      </label>
      <input
        {...register('confirmPassword', { required: true })}
        className="rounded-full w-96 h-10"
        type="password"
        autoComplete="off"
      />
      {errors.confirmPassword?.message ? '비밀번호가 맞지 않습니다.' : ''}
      <Link href="/authenticationNumber">
        <div className="mx-auto rounded-full w-96 h-10 space-y-2">
          <Button>가입하기</Button>
        </div>
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
