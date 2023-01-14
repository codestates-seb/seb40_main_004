import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { GetServerSideProps, NextPage } from 'next';
import { Seo } from '../../components/common/Seo';
import { SignUpForm } from '../../components/signup/SignupForm';

const SignUp: NextPage = () => {
  return (
    <>
      <Seo title="회원 가입" />
      <div className="h-screen">
        <Header />
        <main className="flex flex-col justify-center items-center h-[90%] bg-white">
          <article className="text-center">
            <h3 className="font-bold text-2xl text-font-gray">회원가입</h3>
            <h3 className="mt-4 text-font-gray">
              따뜻한 개발 문화에 동참하세요!
            </h3>
            <SignUpForm />
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

export default SignUp;
