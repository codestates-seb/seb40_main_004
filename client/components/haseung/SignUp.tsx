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
