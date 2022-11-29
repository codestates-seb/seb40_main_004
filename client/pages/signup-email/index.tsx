/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-15
 * 최근 수정일: 2022-11-22
 */

import axios from 'axios';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { useRecoilState, useRecoilValue } from 'recoil';
import { userAuthKey, userEmailAtom } from '../../atomsHS';
import { AuthenticationTimer } from '../../components/haseung/AuthenticationTimer';
import { Intro } from '../../components/haseung/Intro';

type VerificationNumber = {
  authKey: string;
};

const AuthenticateNumber = () => {
  const { register, handleSubmit } = useForm<VerificationNumber>();
  const email = useRecoilValue(userEmailAtom);

  const [authKey, setAuthKey] = useRecoilState(userAuthKey);
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
    <main className="flex justify-center text-lg">
      <article className="text-center mt-10">
        <Intro />
        <section className="text-background-gray font-semibold flex justify-center items-center bg-main-gray w-[400px] h-20 mt-10 rounded-full">
          이메일로 인증번호가 전송되었습니다!
        </section>
        <section className="flex justify-between mt-3">
          <span>인증번호</span>
          <AuthenticationTimer />
        </section>
        <form onSubmit={handleSubmit(onValid)}>
          <input
            {...register('authKey', { required: true })}
            type="text"
            placeholder="1234"
            className="placeholder:px-3 flex justify-center items-center w-[400px] h-12 mt-10 rounded-full"
          />
          <button className="bg-main-yellow font-semibold hover:bg-main-orange placeholder:px-3 flex justify-center items-center w-[400px] h-12 mt-10 rounded-full">
            인증하기
          </button>
        </form>
      </article>
    </main>
  );
};

export default AuthenticateNumber;
