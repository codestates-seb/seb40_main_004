import React from 'react';
import { useForm } from 'react-hook-form';

type OnChangeProps = {
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
};

const TitleInputBox = ({ onChange }: OnChangeProps) => {
  const { register } = useForm();
  return (
    <input
      {...register('title', {
        required: '제목을 입력해주세요!',
        minLength: {
          value: 5,
          message: '제목은 최소 5글자 이상 작성해주세요!🤭',
        },
      })}
      onChange={onChange}
      type="text"
      className="border-2 px-2 py-1 leading-loose flex w-full justify-center rounded-md"
      placeholder="제목을 입력해주세요!"
    />
  );
};

export default TitleInputBox;
