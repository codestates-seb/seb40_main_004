/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-01(박혜정)
 */

import axios from 'axios';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import jwt_decode from 'jwt-decode';
import { useSetRecoilState } from 'recoil';
import { isLoginAtom } from '../../atomsYW';
import { SocialLoginBtn } from './SocialLoginBtn';

type LoginProps = {
  email: string;
  password: string;
};

type DecodedProps = {
  sub: string;
  id: number;
  nickname: string;
};

export const LoginForm = () => {
  const setIsLogin = useSetRecoilState(isLoginAtom);
  const router = useRouter();
  const onValid = ({ email, password }: LoginProps) => {
    axios
      .post(`/api/auth/token`, {
        email,
        password,
      })
      .then((res) => {
        const accessToken = res.data.accessToken;
        const refreshToken = res.data.refreshToken;
        const avatarPath = res.data.avatarPath;
        const decoded: DecodedProps = jwt_decode(accessToken);
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('avatarPath', avatarPath);
        localStorage.setItem('email', decoded.sub);
        localStorage.setItem('userId', String(decoded.id));
        localStorage.setItem('nickname', decoded.nickname);
        setIsLogin(true);
        router.push('/');
      })
      .catch((err) => {
        console.error(err);
        alert('로그인에 실패했습니다...! 다시 한 번 확인해주세요.🥲');
      });
  };
  const { register, handleSubmit } = useForm<LoginProps>();
  return (
    <form
      className="flex flex-col mx-auto justify-center items-start mt-10 "
      onSubmit={handleSubmit(onValid)}
    >
      <label className="font-bold">이메일</label>
      <input
        {...register('email', { required: true })}
        className="rounded-full w-96 h-10 
        pl-4
        border
        mb-5
        "
        type="text"
        placeholder="이메일을 입력해주세요."
      />
      <label className="font-bold">비밀번호</label>
      <input
        {...register('password', { required: true })}
        className="rounded-full w-96 h-10 pl-4
        border
        mb-5
        "
        type="password"
        autoComplete="off"
        placeholder="비밀번호를 입력해주세요."
      />
      <button
        type="submit"
        className="bg-main-yellow py-3 w-full rounded-[20px] font-bold mb-5"
      >
        로그인
      </button>
      <Link href="/edit-password">
        <span className="text-xs mt-3 cursor-pointer hover:text-main-gray">
          비밀번호를 잊어버리셨나요?
        </span>
      </Link>
      <span className="text-xs mt-3 cursor-pointer text-main-gray">
        계정이 없으신가요?{' '}
        <Link href="/signup">
          <span className="text-blue-500 hover:text-blue-400">
            → 회원가입 하러가기
          </span>
        </Link>
      </span>
      <SocialLoginBtn />
    </form>
  );
};
