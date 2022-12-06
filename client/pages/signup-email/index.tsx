/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-15
 * 최근 수정일: 2022-12-01(박혜정)
 */

import axios from 'axios';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { userAuthKey, userEmailAtom } from '../../atomsHS';
import { AuthenticationTimer } from '../../components/haseung/AuthenticationTimer';
import { Intro } from '../../components/haseung/Intro';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { GetServerSideProps, NextPage } from 'next';
import { Seo } from '../../components/common/Seo';

type VerificationNumber = {
  authKey: string;
};

const SignUpWithEmail: NextPage = () => {
  const { register, handleSubmit } = useForm<VerificationNumber>();
  const email = useRecoilValue(userEmailAtom);

  const setAuthKey = useSetRecoilState(userAuthKey);
  const router = useRouter();
  const onValid = ({ authKey }: VerificationNumber) => {
    axios
      .put(`/api/auth/mail`, { email, authKey })
      .then((res) => {
        console.log('res', res);
        setAuthKey(res.data.authKey);
      })
      .catch((error) => console.error('error', error));
    router.push('/signup-status');
  };
  return (
    <>
      <Seo title="회원 가입 - 인증번호 확인" />
      <div className="h-screen">
        <Header />
        <main className="flex flex-col justify-center items-center h-[79vh] bg-white">
          <article className="text-center mt-10 flex flex-col justify-center items-center w-96">
            <Intro />
            <section className="text-background-gray font-semibold flex justify-center items-center bg-main-gray w-full h-20 mt-10 rounded-[20px]">
              이메일로 인증번호가 전송되었습니다!
            </section>
            <section className="flex justify-between w-full mt-10">
              <span className="font-bold">인증번호</span>
              <AuthenticationTimer />
            </section>
            <form onSubmit={handleSubmit(onValid)} className="w-full">
              <input
                {...register('authKey', { required: true })}
                type="text"
                placeholder="1234"
                className="rounded-full w-full h-10 pl-4
              border
              my-5"
              />
              <button className="bg-main-yellow py-3 w-full rounded-[20px] font-bold mb-5">
                인증하기
              </button>
            </form>
          </article>
        </main>
        <Footer />
      </div>
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

export default SignUpWithEmail;
