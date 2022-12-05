import axios from 'axios';
import { useRouter } from 'next/router';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Footer } from '../../../components/common/Footer';
import { Header } from '../../../components/common/Header';
import { Loader } from '../../../components/common/Loader';
import { AuthProps } from '../../../libs/interfaces';

const GetAuthCode = () => {
  const { register, handleSubmit } = useForm<AuthProps>({
    mode: 'onChange',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const router = useRouter();
  const onValid = ({ email }: AuthProps) => {
    axios
      .post(`/api/auth/password/support`, { email })
      .then(() => {
        setIsSubmitting(true);
        router.push('/find-password/check-auth-code');
      })
      .catch((error) => console.error('error', error));
  };
  return (
    <>
      <Header />
      <main className="flex flex-col justify-center items-center h-[79vh] bg-white">
        <form onSubmit={handleSubmit(onValid)} className="space-y-2">
          <label className="font-bold flex-col flex mx-2 mb-2">이메일</label>
          <input
            {...register('email', { required: true })}
            type="text"
            placeholder="이메일을 입력하세요."
            className="rounded-full w-full h-10 pl-4 border"
          />
          <button className="bg-main-yellow bg-opacity-80 py-3 w-full rounded-[20px] font-bold  hover:bg-main-yellow">
            인증번호 발송
          </button>
          <p className="text-center relative top-20 font-bold text-xl">
            {isSubmitting ? (
              <>
                <Loader /> <span>인증번호 전송 중....</span>
              </>
            ) : null}
          </p>
        </form>
      </main>
      <Footer />
    </>
  );
};

export default GetAuthCode;
