import axios from 'axios';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import jwt_decode from 'jwt-decode';
import { useSetRecoilState } from 'recoil';
import { useState } from 'react';
import { toast } from 'react-toastify';
import { DecodedProps } from '../../../libs/interfaces';
import { isLoginAtom } from '../../../atomsYW';
import { Input } from '../../common/Input';

import { Loader } from '../../common/Loader';
import { SocialLoginBtn } from '../../common/SocialLoginBtn';

type LoginProps = {
  email: string;
  password: string;
};

export const LoginForm = () => {
  const setIsLogin = useSetRecoilState(isLoginAtom);
  const [isSubmitting, setIsSubmitting] = useState(false);
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
        setIsSubmitting(true);
        setIsLogin(true);
        toast.success('로그인 성공!');
        router.push('/');
      })
      .catch((err) => {
        console.error(err);
        toast.error('로그인에 실패했습니다! 다시 한 번 확인해주세요.🥲');
      });
  };
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginProps>();
  return (
    <>
      <form
        className="flex flex-col mx-auto justify-center items-start mt-10 "
        onSubmit={handleSubmit(onValid)}
      >
        <div className="space-y-4">
          <Input
            label="이메일"
            type="email"
            placeholder="이메일을 입력해주세요."
            register={{
              ...register('email', { required: '이메일을 입력해주세요!' }),
            }}
            errors={errors.email?.message}
          />
          <Input
            label="비밀번호"
            type="password"
            placeholder="비밀번호를 입력해주세요."
            register={{
              ...register('password', { required: '비밀번호를 입력해주세요!' }),
            }}
            errors={errors.password?.message}
          />
        </div>
        <button
          type="submit"
          className="bg-main-yellow bg-opacity-80 hover:bg-opacity-100 p-3 w-full rounded-[20px] font-bold my-5"
        >
          로그인
        </button>
        <Link href="/find-password/get-auth-code">
          <span className="text-xs my-3 cursor-pointer hover:text-main-gray">
            비밀번호를 잊어버리셨나요?
          </span>
        </Link>
        <span className="text-xs cursor-pointer text-main-gray mb-6">
          계정이 없으신가요?{' '}
          <Link href="/signup">
            <span className="text-blue-500 hover:text-blue-400">
              → 회원가입 하러가기
            </span>
          </Link>
        </span>
        <SocialLoginBtn />
        <p className="text-center relative top-20 mx-auto font-bold text-xl">
          {isSubmitting ? (
            <>
              <Loader /> <span>로그인 중....</span>
            </>
          ) : null}
        </p>
      </form>
    </>
  );
};
