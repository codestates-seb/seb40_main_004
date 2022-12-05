/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-05
 */

import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { renderingAtom, userDashboardAtom } from '../../../atomsYW';
import { Footer } from '../../../components/common/Footer';
import { Header } from '../../../components/common/Header';
import { AsideBot } from '../../../components/yeonwoo/AsideBot';
import { AsideMid } from '../../../components/yeonwoo/AsideMid';
import { AsideTop } from '../../../components/yeonwoo/AsideTop';
import { CarouselArticle } from '../../../components/yeonwoo/CarouselArticle';
import { CarouselReview } from '../../../components/yeonwoo/CarouselReview';
import { Grass } from '../../../components/yeonwoo/Grass';
import { client } from '../../../libs/client';

const Dashboard: NextPage = () => {
  const rendering = useRecoilValue(renderingAtom);
  const setUserDashboard = useSetRecoilState(userDashboardAtom);
  const [userId, setUserId] = useState<string | string[] | undefined>('');
  const router = useRouter();
  const getUser = async () => {
    try {
      if (userId) {
        const res = await client.get(`/api/users/${userId}/dashboard`);
        setUserDashboard(res.data);
      }
    } catch (error) {
      setUserDashboard({
        userId: 0,
        email: '',
        nickname: '탈퇴한 유저',
        jobType: '',
        grade: '',
        point: 0,
        github: '',
        blog: '',
        infoMessage: '',
        rank: 0,
        avatar: {
          avatarId: 0,
          filename: '',
          remotePath: '/favicon.ico',
        },
        tags: [],
        reviewBadges: [],
        articles: [],
        activities: [],
        reviews: [],
      });
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
      <Header />
      <main className="w-[1280px] min-h-screen mx-auto flex gap-12 mb-12">
        <div className="w-[305px]">
          <AsideTop />
          <AsideMid />
          <AsideBot />
        </div>
        <div className="w-full">
          {/* <Grass /> */}
          <div className="mb-8 flex items-baseline">
            <div className="border-b-2 border-main-orange py-4 pr-6">
              <Link href={`/dashboard/${router.query.userId}`}>
                <span className="text-xl font-semibold hover:cursor-pointer">
                  ❓ 나의 질문
                </span>
              </Link>
            </div>
            <div className="border-b-2 py-4 pr-6">
              <Link href={`/dashboard/${router.query.userId}/answers`}>
                <span className="text-xl font-semibold hover:cursor-pointer">
                  ❗ 나의 답변
                </span>
              </Link>
            </div>
            <div className="border-b-2 py-4 pr-6">
              <Link href={`/dashboard/${router.query.userId}/bookmarks`}>
                <span className="text-xl font-semibold hover:cursor-pointer">
                  🔖 북마크
                </span>
              </Link>
            </div>
          </div>
          <CarouselArticle />
          <div className="mt-20 mb-8">
            <span className="text-xl font-semibold">☀️ 응원 메세지</span>
          </div>
          <CarouselReview />
        </div>
      </main>
      <Footer />
    </>
  );
};

export default Dashboard;
