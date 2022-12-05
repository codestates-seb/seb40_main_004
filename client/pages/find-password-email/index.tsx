import axios from 'axios';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';

type FindPasswordProps = {
  email: string;
};

const findPassWordWithEmail = () => {
  const { register, handleSubmit } = useForm<FindPasswordProps>({
    mode: 'onChange',
  });
  const router = useRouter();
  const onValid = ({ email }: FindPasswordProps) => {
    axios
      .post(`/api/auth/password/support`, { email })
      .then((res) => {
        console.log('res1', res);
        router.push('/temporary-password');
      })

      .catch((error) => console.error('error', error));
  };
  return (
    <form onSubmit={handleSubmit(onValid)}>
      <input
        {...register('email', { required: true })}
        type="text"
        placeholder="이메일을 입력하세요."
        className="rounded-full w-full h-10 pl-4 border my-5"
      />
      <button className="bg-main-yellow py-3 flex justify-center mx-auto w-1/2 rounded-[20px] font-bold mb-5">
        인증번호 발송
      </button>
    </form>
  );
};

export default findPassWordWithEmail;
