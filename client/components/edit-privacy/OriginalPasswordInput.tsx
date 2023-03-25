// import React from 'react';
import { useForm } from 'react-hook-form';

const OriginalPasswordInput = () => {
  const { register } = useForm();
  return (
    <input
      id="original-password"
      type="password"
      className="w-full rounded-full h-11 px-4 border border-main-gray"
      placeholder="기존 비밀번호 입력"
      {...register('originalPassword', {
        required: '기존 비밀번호는 필수 입력 사항입니다',
        pattern:
          /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/i,
      })}
    />
  );
};

export default OriginalPasswordInput;
