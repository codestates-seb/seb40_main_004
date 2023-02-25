import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useRecoilState, useRecoilValue } from 'recoil';

import { renderingAtom } from '@atoms/renderingAtom';
import { userDashboardAtom } from '@atoms/userAtom';

import { Footer } from '@components/common/Footer';
import { Header } from '@components/common/Header';
import { Seo } from '@components/common/Seo';
import { CarouselAnswers } from '@components/dashboard/CarouselAnswers';
import { CarouselReview } from '@components/dashboard/CarouselReview';

import { client } from '@libs/client';

import AsideComponent from '@components/dashboard/AsideComponent';
import { defaultAvatar, defaultUserDashboard } from './ defaultUserDashboard';

const className = 'border-b-2 py-4 pr-6';
const cursorClassName = 'text-xl font-semibold hover:cursor-pointer';

const DashboardAnswers: NextPage = () => {
  const rendering = useRecoilValue(renderingAtom);
  const [userDashboard, setUserDashboard] = useRecoilState(userDashboardAtom);
  const setDefaultUserDashboard = () =>
    setUserDashboard(defaultAvatar && defaultUserDashboard);
  const [userId, setUserId] = useState<string | string[] | undefined>('');
  const router = useRouter();
  const getUser = async () => {
    try {
      if (userId) {
        const res = await client.get(`/api/users/${userId}/dashboard`);
        setUserDashboard(res.data);
      }
    } catch (error) {
      setDefaultUserDashboard();
    }
  };

  useEffect(() => {
    setUserId(router.query.userId);
  });

  useEffect(() => {
    getUser();
  }, [userId, rendering]);

  return (
    <>
      <Seo
        title={
          userDashboard.nickname !== '탈퇴한 유저'
            ? userDashboard.nickname
            : '대시보드'
        }
      />
      <Header />
      <main className="w-[1280px] min-h-screen mx-auto flex gap-12 mb-12">
        <AsideComponent />
        <article className="w-full">
          {/* <Grass /> */}
          <section className="mb-8 flex items-baseline">
            <div className={className}>
              <Link href={`/dashboard/${router.query.userId}`}>
                <span className={cursorClassName}>❓ 나의 질문</span>
              </Link>
            </div>
            <div className={`border-main-orange ${className}`}>
              <Link href={`/dashboard/${router.query.userId}/answers`}>
                <span className={cursorClassName}>❗ 나의 답변</span>
              </Link>
            </div>
            <div className={className}>
              <Link href={`/dashboard/${router.query.userId}/bookmarks`}>
                <span className={cursorClassName}>🔖 북마크</span>
              </Link>
            </div>
          </section>
          <CarouselAnswers />
          <div className="mt-20 mb-8">
            <span className="text-xl font-semibold">☀️ 응원 메세지</span>
          </div>
          <CarouselReview />
        </article>
      </main>
      <Footer />
    </>
  );
};

export default DashboardAnswers;
