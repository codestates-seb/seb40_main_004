/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-18
 */

import { NextPage } from 'next';
import { Footer } from '../components/common/Footer';
import { Header } from '../components/common/Header';
import { CarouselMain } from '../components/yeonwoo/CarouselMain';
import { CheerUp } from '../components/yeonwoo/CheerUp';
import { HallOfFame } from '../components/yeonwoo/HallOfFame';
import { ListLately } from '../components/yeonwoo/ListLately';
import dynamic from 'next/dynamic';

const Calendar = dynamic(
  () => import('../components/yeonwoo/Calendar').then((mod) => mod.Calendar),
  {
    ssr: false,
  },
);

const Home: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] min-h-screen mx-auto">
        <div className="w-[1280px] h-[350px] mx-auto relative flex justify-center items-center">
          <CarouselMain />
        </div>
        <div className="w-[1280px] py-12 flex justify-center">
          <CheerUp />
        </div>
        <div className="w-[1163px] h-[581px] mx-auto rounded-2xl bg-white py-10 px-14">
          <ListLately />
        </div>
        <div className="w-[1163px] py-12 mx-auto flex gap-14">
          <div>
            <div className="w-[732px] h-[60px] bg-main-yellow rounded-2xl mb-[22px] flex justify-center items-center">
              <span className="text-2xl font-bold">📆 채용 일정 캘린더</span>
            </div>
            <div className="w-[732px] h-[592px] bg-white rounded-2xl pt-3">
              <Calendar />
            </div>
          </div>
          <div className="w-[380px] h-[674px] bg-white rounded-2xl py-8 px-5">
            <HallOfFame />
          </div>
        </div>
      </main>
      <Footer />
    </>
  );
};

export default Home;
