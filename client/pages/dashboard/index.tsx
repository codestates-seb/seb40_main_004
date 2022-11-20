/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-19
 */
import { NextPage } from 'next';
import { Footer } from '../../components/common/Footer';
import { Header } from '../../components/common/Header';
import { AsideBot } from '../../components/yeonwoo/AsideBot';
import { AsideMid } from '../../components/yeonwoo/AsideMid';
import { AsideTop } from '../../components/yeonwoo/AsideTop';
import { CarouselArticle } from '../../components/yeonwoo/CarouselArticle';
import { CarouselReview } from '../../components/yeonwoo/CarouselReview';
import { Grass } from '../../components/yeonwoo/Grass';

const Dashboard: NextPage = () => {
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
          <Grass />
          <div className="mb-8 flex items-baseline">
            <div className="border-b-2 border-main-orange py-4 pr-6">
              <span className="text-2xl font-semibold">❓ 나의 질문</span>
            </div>
            <div className="border-b-2 py-4 pr-6">
              <span className="text-2xl font-semibold">❗ 나의 답변</span>
            </div>
            <div className="border-b-2 py-4 pr-6">
              <span className="text-2xl font-semibold">🔖 북마크</span>
            </div>
            <div>
              <span className="text-xs ml-4">더 보기 ＞</span>
            </div>
          </div>
          <CarouselArticle />
          <div className="mt-20 mb-8">
            <span className="text-2xl font-semibold">☀️ 응원 메세지</span>
            <span className="text-xs ml-4">더 보기 ＞</span>
          </div>
          <CarouselReview />
        </div>
      </main>
      <Footer />
    </>
  );
};

export default Dashboard;
