import { Intro } from '../../components/haseung/Intro';
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
            <Intro />
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
