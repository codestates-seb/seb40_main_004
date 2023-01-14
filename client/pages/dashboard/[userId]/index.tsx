import { GetServerSideProps, NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useRecoilValue, useRecoilState } from 'recoil';
import { renderingAtom, userDashboardAtom } from '../../../atomsYW';
import { Footer } from '../../../components/common/Footer';
import { Header } from '../../../components/common/Header';
import { Seo } from '../../../components/common/Seo';
import { AsideBot } from '../../../components/dashboard/AsideBot';
import { AsideMid } from '../../../components/dashboard/AsideMid';
import { AsideTop } from '../../../components/dashboard/AsideTop';
import { CarouselArticle } from '../../../components/dashboard/CarouselArticle';
import { CarouselReview } from '../../../components/dashboard/CarouselReview';
import { client } from '../../../libs/client';

const Dashboard: NextPage = () => {
  const rendering = useRecoilValue(renderingAtom);
  const [userDashboard, setUserDashboard] = useRecoilState(userDashboardAtom);
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
      <Seo
        title={
          userDashboard.nickname !== '탈퇴한 유저'
            ? userDashboard.nickname
            : '대시보드'
        }
      />
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

export const getServerSideProps: GetServerSideProps = async (context) => {
  const content = context.req.url?.split('/')[1];
  return {
    props: {
      content,
    },
  };
};

export default Dashboard;
