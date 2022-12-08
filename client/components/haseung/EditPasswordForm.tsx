/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-18
 */

import axios from 'axios';
import { useForm } from 'react-hook-form';
import { Button } from '../common/Button';

type PasswordProps = {
  newpassword: string;
  checknewpassword: string;
};

export const EditPasswordForm = () => {
  const onValid = ({ newpassword, checknewpassword }: PasswordProps) => {
    /* Sending a post request to the server. */
    // console.log(email, password);
    // fetch('http://localhost:3000/users', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ email, password }),
    // })
    //   .then((res) => res.json())
    //   .then((data) => console.log('data', data.user));
    axios
      .post('http://localhost:3000/users', { newpassword, checknewpassword })
      .then((res) => console.log('res', res.data));
  };
  const { register, handleSubmit } = useForm<PasswordProps>();
  return (
    <form
      className="flex flex-col mx-auto justify-center items-start mt-10"
      onSubmit={handleSubmit(onValid)}
    >
      <label>새 비밀번호</label>
      <input
        {...register('newpassword', { required: true })}
        className="rounded-full w-96 h-10 placeholder:text-base placeholder:pl-3 placeholder:pb-2"
        type="password"
        autoComplete="off"
      />
      <label>비밀번호 확인</label>
      <input
        {...register('checknewpassword', { required: true })}
        className="rounded-full w-96 h-10"
        type="password"
        autoComplete="off"
      />
      <div className="mx-auto mt-4 rounded-full w-96 h-10 ">
        <Button>비밀번호 변경하기</Button>
      </div>
    </form>
  );
};
