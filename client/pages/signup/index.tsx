/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-15
 * 개요: 회원가입에 대한 form을 표시합니다.
 */

import { Intro } from '../../components/haseung/Intro';
import { SignUpForm } from '../../components/haseung/SignUpForm';

const SignUp = () => {
  return (
    <main className="flex justify-center text-lg">
      <article className="text-center">
        <Intro />
        <SignUpForm />
      </article>
    </main>
  );
};

export default SignUp;
