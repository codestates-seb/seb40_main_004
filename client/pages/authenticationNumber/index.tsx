/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-15
 * 최근 수정일: 2022-11-15
 */

import Link from 'next/link';
import { AuthenticationTimer } from '../../components/haseung/AuthenticationTimer';
import { Intro } from '../../components/haseung/Intro';

const AuthenticateNumber = () => {
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
        <input
          type="text"
          placeholder="1234"
          className="placeholder:px-3 flex justify-center items-center w-[400px] h-12 mt-10 rounded-full"
        />

        <p className="flex justify-start mt-3 text-red-500">
          인증번호를 입력해주세요.
        </p>
        <Link href="/selectStatus">
          <button className="bg-main-yellow font-semibold hover:bg-main-orange placeholder:px-3 flex justify-center items-center w-[400px] h-12 mt-10 rounded-full">
            인증하기
          </button>
        </Link>
      </article>
    </main>
  );
};

export default AuthenticateNumber;
