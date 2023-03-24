import { useForm } from 'react-hook-form';

const NewPasswordInput = () => {
  const { register } = useForm();
  return (
    <input
      id="new-password"
      type="password"
      className="w-full rounded-full h-11 px-4 border border-main-gray"
      placeholder="신규 비밀번호 입력"
      {...register('newPassword', {
        required: '신규 비밀번호는 필수 입력 사항입니다',
        pattern:
          /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\d~!@#$%^&*()+|=]{8,16}$/i,
      })}
    />
  );
};

export default NewPasswordInput;
