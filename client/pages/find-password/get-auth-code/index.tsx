import axios from 'axios';
import { GetServerSideProps, NextPage } from 'next';
import { useRouter } from 'next/router';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { Footer } from '../../../components/common/Footer';
import { Header } from '../../../components/common/Header';
import { Loader } from '../../../components/common/Loader';
import { Seo } from '../../../components/common/Seo';
import { AuthResp } from '../../../types/login';

const GetAuthCode: NextPage = () => {
  const { register, handleSubmit } = useForm<AuthResp>({
    mode: 'onChange',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const router = useRouter();
  const onValid = ({ email }: AuthResp) => {
    axios
      .post(`/api/auth/password/support`, { email })
      .then(() => {
        setIsSubmitting(true);
        toast.success('인증번호가 발송되었습니다! 메일을 확인해주세요😉');
        router.push('/find-password/check-auth-code');
      })
      .catch((error) => {
        console.error('error', error);
        toast.error('이메일이 일치하지 않습니다..! 다시 한 번 확인해주세요.🥲');
      });
  };
  return (
    <>
      <Seo title="비밀번호 찾기 - 인증번호 발송" />
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

export const getServerSideProps: GetServerSideProps = async (context) => {
  const content = context.req.url?.split('/')[1];
  return {
    props: {
      content,
    },
  };
};

export default GetAuthCode;
