import axios from 'axios';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { useRecoilState, useRecoilValue } from 'recoil';
import { GetServerSideProps, NextPage } from 'next';
import { useState } from 'react';
import { toast } from 'react-toastify';

import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { Seo } from '../../components/common/Seo';
import { AuthenticationTimer } from '../../components/signup-email/AuthenticationTimer';
import {
  userAuthKey,
  userEmailAtom,
  userNickName,
  userPassword,
} from '../../atoms/userAtom';

type VerificationNumber = {
  authKey: string;
};

const SignUpWithEmail: NextPage = () => {
  const [isOkAuthCode, setIsOkAuthCode] = useState(false);
  const { register, handleSubmit } = useForm<VerificationNumber>();
  const [authKey, setAuthKey] = useRecoilState(userAuthKey);
  const email = useRecoilValue(userEmailAtom);
  const password = useRecoilValue(userPassword);
  const nickname = useRecoilValue(userNickName);
  const router = useRouter();
  const onValid = ({ authKey }: VerificationNumber) => {
    axios
      .put(`/api/auth/mail`, { email, authKey })
      .then((res) => {
        setAuthKey(res.data.authKey);
        setIsOkAuthCode(true);
      })
      .catch((error) => console.error('error', error));
  };
  const onClickSignUp = (e: React.MouseEvent<HTMLElement>) => {
    e.preventDefault();
    axios
      .post(`/api/auth`, {
        email,
        authKey,
        password,
        nickname,
      })
      .then((res) => {
        console.log('res1', res);
        toast.success('가입이 완료되었습니다! 로그인 페이지로 이동할게요.😉');
      })
      .catch((error) => {
        console.error('error', error);
        toast.error(
          '인증번호가 올바르지 않습니다..! 다시 한 번 확인해주세요.🥲',
        );
      });

    router.push('/login');
  };
  return (
    <>
      <Seo title="회원 가입 - 인증번호 확인" />
      <div className="h-screen">
        <Header />
        <main className="flex flex-col justify-center items-center h-[79vh] bg-white">
          <article className="text-center mt-10 flex flex-col justify-center items-center w-96">
            <h3 className="font-bold text-2xl text-font-gray">이메일 인증</h3>
            <h3 className="mt-4 text-font-gray">
              따뜻한 개발 문화에 동참하세요!
            </h3>
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
              {!isOkAuthCode ? (
                <>
                  <button className="py-3 w-full rounded-[20px] font-bold mb-5 bg-main-yellow">
                    인증하기
                  </button>
                  <button
                    disabled
                    type="button"
                    onClick={onClickSignUp}
                    className="py-3 w-full rounded-[20px] font-bold mb-5 bg-main-gray"
                  >
                    가입하기
                  </button>
                </>
              ) : (
                <>
                  <button
                    disabled
                    className="py-3 w-full rounded-[20px] font-bold mb-5 bg-main-gray"
                  >
                    인증하기
                  </button>
                  <button
                    type="button"
                    onClick={onClickSignUp}
                    className="py-3 w-full rounded-[20px] font-bold mb-5 bg-main-yellow"
                  >
                    가입하기
                  </button>
                </>
              )}
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
