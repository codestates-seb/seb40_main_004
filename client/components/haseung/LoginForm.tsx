/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-22
 */

import axios from 'axios';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { useRecoilState } from 'recoil';
import { isLoggedInAtom } from '../../atomsYW';
import { Button } from '../common/Button';

type LoginProps = {
  email: string;
  password: string;
};

export const LoginForm = () => {
  const router = useRouter();
  const [isLoggedIn, setIsLoggedIn] = useRecoilState(isLoggedInAtom);
  const onValid = ({ email, password }: LoginProps) => {
    axios
      .post(`/api/auth/token`, {
        email,
        password,
      })
      .then((res) => {
        if (res.data.authorization) {
          localStorage.setItem('loginToken', JSON.stringify(res.data));
        }
      })
      .then(() => {
        setIsLoggedIn(!isLoggedIn);
        alert('로그인 성공');
        router.push('/');
      });
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
      <div className="mx-auto mt-4 rounded-full w-96 h-10 ">
        <Button>로그인</Button>
      </div>
    </form>
  );
};
