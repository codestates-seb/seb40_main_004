/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-15
 * 최근 수정일: 2022-12-01(박혜정)
 */

import axios from 'axios';
import { useRouter } from 'next/router';
import { useRecoilValue } from 'recoil';
import {
  userAuthKey,
  userEmailAtom,
  userNickName,
  userPassword,
} from '../../atomsHS';
import { Intro } from '../../components/haseung/Intro';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { GetServerSideProps, NextPage } from 'next';
import Head from 'next/head';
import { TitleProps } from '../../libs/interfaces';

const SelectStatus: NextPage<TitleProps> = ({ title = '직업 선택' }) => {
  const email = useRecoilValue(userEmailAtom);
  const authKey = useRecoilValue(userAuthKey);
  const password = useRecoilValue(userPassword);
  const nickname = useRecoilValue(userNickName);

  const router = useRouter();
  const onSelectedStatusClick = (e: React.MouseEvent<HTMLElement>) => {
    e.preventDefault();
    axios
      .post(`/api/auth`, {
        email,
        authKey,
        password,
        nickname,
      })
      .then((res) => console.log('res1', res))
      .catch((error) => console.error('error', error));
    alert('가입이 완료되었습니다! 로그인 페이지로 이동할게요.😉');
    router.push('/login');
  };

  return (
    <div className="h-screen">
      <Head>
        <title>{title}</title>
      </Head>
      <Header />
      <form className="flex flex-col justify-center items-center h-[79vh] bg-white">
        <article className="text-center mt-10">
          <Intro />
          <section className="text-background-gray font-semibold flex justify-center items-center bg-main-gray w-full h-20 mt-10 rounded-[20px]">
            마지막 단계입니다! 회원님에 대해서 알려주세요!
          </section>
          <fieldset className="flex justify-start flex-col mt-10 leading-8">
            <div>
              <input type="radio" name="check" value="newbie" />
              <label htmlFor="개발자 취준생">개발자 취준생</label>
            </div>
            <div>
              <input type="radio" name="check" value="junior" />
              <label htmlFor="현업 개발자">현업 개발자</label>
            </div>
            <div>
              <input type="radio" name="check" value="general" />
              <label htmlFor="개발에 관심있는 일반인">
                개발에 관심있는 일반인
              </label>
            </div>
          </fieldset>
          <div
            onClick={onSelectedStatusClick}
            className="ml-2 flex justify-center items-center w-[400px] h-12 mt-10 rounded-full"
          >
            <button
              onClick={onSelectedStatusClick}
              className="bg-main-yellow py-3 w-full rounded-[20px] font-bold mb-5"
            >
              인증하기
            </button>
          </div>
        </article>
      </form>
      <Footer />
    </div>
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

export default SelectStatus;
