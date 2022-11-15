/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 * 개요: 회원가입에 대한 form을 표시합니다.
 */

import { SignUpForm } from './SignUpForm';

export const SignUp = () => {
  return (
    <main className="flex justify-center text-lg">
      <article className="text-center">
        <h3 className="font-bold">회원가입</h3>
        <h3 className="mt-5">따뜻한 개발 문화에 동참하세요!</h3>
        <SignUpForm />
      </article>
    </main>
  );
};
