/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-28
 */
import axios from 'axios';
import { NextPage } from 'next';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useRecoilState } from 'recoil';
import { userDashboardAtom } from '../../../atomsYW';
import { Footer } from '../../../components/common/Footer';
import { Header } from '../../../components/common/Header';
import { AsideBot } from '../../../components/yeonwoo/AsideBot';
import { AsideMid } from '../../../components/yeonwoo/AsideMid';
import { AsideTop } from '../../../components/yeonwoo/AsideTop';
import { CarouselBookmarks } from '../../../components/yeonwoo/CarouselBookmarks';
import { CarouselReview } from '../../../components/yeonwoo/CarouselReview';
import { Grass } from '../../../components/yeonwoo/Grass';

const DashboardBookmarks: NextPage = () => {
  const [userId, setUserId] = useState<string | string[] | undefined>('');
  const [userDashboard, setUserDashboard] = useRecoilState(userDashboardAtom);
  const router = useRouter();
  const getUser = async () =>
    await axios
      .get(`/api/users/${userId}/dashboard`, {
        headers: {
          'ngrok-skip-browser-warning': '111',
        },
      })
      .then((res) => setUserDashboard(res.data))
      .catch((error) => console.log(error));

  useEffect(() => {
    setUserId(router.query.userId);
  });

  useEffect(() => {
    getUser();
  }, [userId]);

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
            <div className="border-b-2 py-4 pr-6">
              <Link href={`/dashboard/${router.query.userId}`}>
                <span className="text-2xl font-semibold hover:cursor-pointer">
                  ❓ 나의 질문
                </span>
              </Link>
            </div>
            <div className="border-b-2 py-4 pr-6">
              <Link href={`/dashboard/${router.query.userId}/answers`}>
                <span className="text-2xl font-semibold hover:cursor-pointer">
                  ❗ 나의 답변
                </span>
              </Link>
            </div>
            <div className="border-b-2 py-4 pr-6 border-main-orange">
              <Link href={`/dashboard/${router.query.userId}/bookmarks`}>
                <span className="text-2xl font-semibold hover:cursor-pointer">
                  🔖 북마크
                </span>
              </Link>
            </div>
            {/* <div>
              <span className="text-xs ml-4 hover:cursor-pointer">
                더 보기 ＞
              </span>
  </div> */}
          </div>
          <CarouselBookmarks />
          <div className="mt-20 mb-8">
            <span className="text-2xl font-semibold">☀️ 응원 메세지</span>
            {/* <span className="text-xs ml-4">더 보기 ＞</span> */}
          </div>
          <CarouselReview />
        </div>
      </main>
      <Footer />
    </>
  );
};

export default DashboardBookmarks;
