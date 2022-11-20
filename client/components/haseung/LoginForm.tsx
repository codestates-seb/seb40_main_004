/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-20
 */

import axios from 'axios';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { Button } from '../common/Button';

type LoginProps = {
  email: string;
  password: string;
};

export const LoginForm = () => {
  const onValid = ({ email, password }: LoginProps) => {
    axios
      .post('http://localhost:3000/users', { email, password })
      .then((res) => console.log('res', res.data));
  };
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginProps>();
  return (
    <form
      className="flex flex-col mx-auto justify-center items-start mt-10"
      onSubmit={handleSubmit(onValid)}
    >
      <label>이메일</label>
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
        placeholder="hyejj19@naver.com"
      />
      <label>비밀번호</label>
      <input
        {...register('password', {
          required: true,
          pattern: {
            value:
              /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/i,
            message: '비밀번호를 확인해주세요.',
          },
        })}
        className="rounded-full w-96 h-10"
        type="password"
        autoComplete="off"
      />
      <p className="font-semibold text-red-500">{errors.password?.message}</p>
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
