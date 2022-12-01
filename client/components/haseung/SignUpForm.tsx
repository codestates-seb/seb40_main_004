/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-22
 * 개요: 가입하기 버튼에 인증번호 발송 페이지로 가는 링크 추가
 */

import axios from 'axios';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { SubmitHandler, useForm } from 'react-hook-form';
import { useRecoilState } from 'recoil';
import { userEmailAtom, userNickName, userPassword } from '../../atomsHS';
import { Button } from '../common/Button';
import { Divider } from './Divider';
import { SocialLoginBtn } from './SocialLoginBtn';

type SignUpProps = {
  email: string;
  password: string;
  confirmPassword?: string;
  nickname: string;
};

export const SignUpForm = () => {
  const [email, setEmail] = useRecoilState(userEmailAtom);
  const [password, setPassword] = useRecoilState(userPassword);
  const [nickname, setNickName] = useRecoilState(userNickName);
  const router = useRouter();
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<SignUpProps>();

  const onValid: SubmitHandler<SignUpProps> = ({
    email,
    password,
    confirmPassword,
    nickname,
  }) => {
    if (password !== confirmPassword) {
      console.log('비밀번호 다름');
      setError(
        'confirmPassword',
        { message: '비밀번호가 맞지 않습니다.' },
        { shouldFocus: true },
      );
    }

    axios
      .post(`/api/auth/mail`, {
        email,
        password,
        confirmPassword,
        nickname,
      })
      .then((res) => {
        setEmail(email);
        setPassword(password);
        setNickName(nickname);
        router.push('/signup-email');
      })
      .catch((error) => {
        console.log('auth error', error);
      });
  };

  return (
    <form
      className="flex flex-col mx-auto justify-center items-start mt-3 space-y-3"
      onSubmit={handleSubmit(onValid)}
    >
      <label htmlFor="nickname" className="font-bold">
        닉네임
      </label>
      <input
        {...register('nickname', {
          required: true,
          minLength: {
            value: 2,
            message: '닉네임은 2자 이상이어야 합니다.',
          },
          maxLength: {
            value: 16,
            message: '닉네임은 16자 이하이어야 합니다.',
          },
        })}
        className="rounded-full w-96 h-10 placeholder:text-base placeholder:pl-3 placeholder:pb-2"
        type="text"
        placeholder="닉네임을 입력해주세요."
      />
      <p className="font-bold text-red-500 text-sm text-center">
        {errors.nickname?.message}
      </p>
      <label htmlFor="email" className="font-bold">
        이메일
      </label>
      <input
        {...register('email', {
          required: true,
          pattern: {
            value: /^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/i,
            message: '이메일이 형식에 맞지 않습니다.',
          },
        })}
        className="rounded-full w-96 h-10 placeholder:text-base placeholder:pl-3 placeholder:pb-2"
        type="text"
        placeholder="이메일을 입력해주세요."
      />
      <p className="text-red-500 font-semibold text-sm text-center">
        {errors.email?.message}
      </p>
      <label htmlFor="password" className="font-bold">
        비밀번호
      </label>
      <input
        {...register('password', {
          required: true,
          pattern: {
            value:
              /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/i,
            message:
              '비밀번호는 8~16자, 영어 대소문자,특수문자가 포함되어야 합니다.',
          },
        })}
        className="rounded-full w-96 h-10 placeholder:text-base placeholder:pl-3"
        type="password"
        placeholder="비밀번호를 입력해주세요."
        autoComplete="off"
      />
      <p className="font-semibold text-red-500 relative right-0 text-sm">
        {errors.password?.message}
      </p>
      <label htmlFor="confirmPassword" className="font-bold">
        비밀번호 확인
      </label>
      <input
        {...register('confirmPassword', {
          required: true,
        })}
        className="rounded-full w-96 h-10 placeholder:text-base placeholder:pl-3"
        type="password"
        placeholder="한번 더 입력해주세요."
        autoComplete="off"
      />
      <p className="font-semibold text-red-500 text-sm text-center">
        {errors.confirmPassword?.message}
      </p>
      <section className="mx-auto rounded-full w-96 h-10 space-y-2">
        <Button>가입하기</Button>
      </section>
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
