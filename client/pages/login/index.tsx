/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-15
 * 개요: 로그인에 대한 form을 표시합니다.
 */

import { LoginForm } from '../../components/haseung/LoginForm';

const Login = () => {
  return (
    <main className="flex justify-center text-lg ">
      <article className="text-center mt-10">
        <h3 className="font-bold">로그인</h3>
        <h3 className="mt-5">다시 오셨군요! 환영합니다.</h3>
        <LoginForm />
      </article>
    </main>
  );
};

export default Login;
