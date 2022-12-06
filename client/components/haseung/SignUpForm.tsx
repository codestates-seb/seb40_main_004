/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-01(박혜정)
 * 개요: 가입하기 버튼에 인증번호 발송 페이지로 가는 링크 추가
 */

import axios from 'axios';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { SubmitHandler, useForm } from 'react-hook-form';
import { useSetRecoilState } from 'recoil';
import { userEmailAtom, userNickName, userPassword } from '../../atomsHS';
import { Input } from '../common/Input';
import { Divider } from './Divider';
import { SocialLoginBtn } from './SocialLoginBtn';

type SignUpProps = {
  email: string;
  password: string;
  confirmPassword?: string;
  nickname: string;
};

export const SignUpForm = () => {
  const setEmail = useSetRecoilState(userEmailAtom);
  const setPassword = useSetRecoilState(userPassword);
  const setNickName = useSetRecoilState(userNickName);
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
      .then(() => {
        setEmail(email);
        setPassword(password);
        setNickName(nickname);
        router.push('/signup-email');
      })
      .catch((error) => {
        console.log('auth error', error);
        alert('회원가입에 실패하였습니다..! 다시 한 번 확인해주세요.🥲');
      });
  };

  return (
    <form
      className="flex flex-col mx-auto justify-center items-start mt-5"
      onSubmit={handleSubmit(onValid)}
    >
      <div className="space-y-4">
        <Input
          label="닉네임"
          type="text"
          placeholder="닉네임을 입력해주세요."
          register={{
            ...register('nickname', {
              required: '닉네임을 입력해주세요.',
              pattern: {
                value: /^(?=.*[a-z0-9가-힣])[a-z0-9가-힣].{0,6}$/,
                message: '자음/모음 1자리 이상, 7자리 이하여야 합니다.',
              },
            }),
          }}
          errors={errors.nickname?.message}
        />

        <Input
          label="이메일"
          type="email"
          placeholder="이메일을 입력해주세요."
          register={{
            ...register('email', {
              required: '이메일을 입력해주세요.',
              pattern: {
                value: /^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/i,
                message: '이메일이 형식에 맞지 않습니다.',
              },
            }),
          }}
          errors={errors.email?.message}
        />

        <Input
          label="비밀번호"
          type="password"
          placeholder="비밀번호를 입력해주세요."
          register={{
            ...register('password', {
              required: '비밀번호를 입력해주세요.',
              pattern: {
                value:
                  /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/i,
                message:
                  '비밀번호는 8~16자, 영어 대소문자,특수문자가 포함되어야 합니다.',
              },
            }),
          }}
          errors={errors.password?.message}
        />

        <Input
          label="비밀번호 확인"
          type="password"
          placeholder="한번 더 입력해주세요."
          register={{
            ...register('confirmPassword', {
              required: '비밀번호를 한번 더 입력해주세요.',
            }),
          }}
          errors={errors.confirmPassword?.message}
        />
      </div>

      <button
        type="submit"
        className="bg-main-yellow bg-opacity-80 hover:bg-opacity-100 p-3 w-full rounded-[20px] font-bold my-5"
      >
        가입하기
      </button>
      <span className="mt-4 text-main-gray text-xs">
        이미 계정이 있으신가요?{' '}
        <Link href="/login">
          <span className="text-blue-500 cursor-pointer hover:text-blue-400">
            로그인
          </span>
        </Link>
      </span>
      <section className="mt-5 w-full">
        <Divider />
        <SocialLoginBtn />
      </section>
    </form>
  );
};
