import { useRef } from 'react';
import { useForm } from 'react-hook-form';

const NewPaswordCheckInput = () => {
  const { register, watch } = useForm();
  const passwordCheck = useRef({});
  passwordCheck.current = watch('newPassword', '');
  return (
    <input
      id="new-password-check"
      type="password"
      className="w-full rounded-full h-11 px-4 border border-main-gray"
      placeholder="신규 비밀번호 확인"
      {...register('newPasswordCheck', {
        required: '신규 비밀번호 확인은 필수 입력 사항입니다',
        validate: (value) =>
          value === passwordCheck.current || '비밀번호가 일치하지 않습니다.',
      })}
    />
  );
};

export default NewPaswordCheckInput;
