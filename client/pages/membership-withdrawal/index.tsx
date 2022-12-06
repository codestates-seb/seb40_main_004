/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-12-01
 * 최근 수정일: 2022-12-01
 */

import { GetServerSideProps, NextPage } from 'next';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect } from 'react';
import { Footer } from '../../components/common/Footer';
import { Header } from '../../components/common/Header';
import { AsideEditProfile } from '../../components/yeonwoo/AsideEditProfile';
import { EditAvatar } from '../../components/yeonwoo/EditAvatar';
import { EditProfileComponent } from '../../components/yeonwoo/EditProfile';
import { TitleProps } from '../../libs/interfaces';

const MembershipWithdrawal: NextPage<TitleProps> = ({
  title = '회원 탈퇴',
}) => {
  const router = useRouter();
  useEffect(() => {
    if (typeof window !== 'undefined') {
      if (!localStorage.getItem('refreshToken')) {
        alert('로그인이 필요한 페이지입니다');
        router.push('/');
      }
    }
  }, []);
  return (
    <>
      <Head>
        <title>{title}</title>
      </Head>
      <Header />
      <main className="w-[1280px] min-h-screen mx-auto flex justify-between mb-12">
        <div className="w-[280px] pt-12">
          <AsideEditProfile />
        </div>
        <div className="w-2/4">
          <EditProfileComponent />
        </div>
        <div className="pt-12 w-1/5"></div>
      </main>
      <Footer />
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

export default MembershipWithdrawal;
