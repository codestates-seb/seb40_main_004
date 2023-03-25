import Link from 'next/link';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import jwt_decode from 'jwt-decode';
import { useSetRecoilState } from 'recoil';
import { useState } from 'react';
import { toast } from 'react-toastify';

import { DecodedResp } from '@type/login';

import { Input } from '@components/common/Input';
import { Loader } from '@components/common/Loader';
import { SocialLoginBtn } from '@components/common/SocialLoginBtn';

import { isLoginAtom } from '@atoms/loginAtom';
import { authentiCate } from 'api/loginApi';

type LoginProps = {
  email: string;
  password: string;
};

export const LoginForm = () => {
  const setIsLogin = useSetRecoilState(isLoginAtom);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const router = useRouter();

  const onValid = async ({ email, password }: LoginProps) => {
    try {
      const res = await authentiCate(email, password);
      const { accessToken, refreshToken, avatarPath } = res.data;
      const decoded: DecodedResp = jwt_decode(accessToken);

      const data = {
        accessToken,
        refreshToken,
        avatarPath,
        email: decoded.sub,
        userId: String(decoded.id),
        nickname: decoded.nickname,
      };

      Object.entries(data).forEach(([key, value]) => {
        localStorage.setItem(key, value);
      });

      setIsLogin(true);
      toast.success('로그인이 완료되었습니다!');
      router.push('/');
    } catch (err) {
      console.error(err);
      toast.error(
        '로그인에 실패했습니다! 아이디 혹은 비밀번호를 다시 확인해주세요.🥲',
      );
    } finally {
      setIsSubmitting(false);
    }
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
            <span className="text-blue-500 hover:text-blue-400 font-bold">
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
