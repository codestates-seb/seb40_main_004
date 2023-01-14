import { NextPage } from 'next';
import { Footer } from '../components/common/Footer';
import { Header } from '../components/common/Header';

import dynamic from 'next/dynamic';
import { Seo } from '../components/common/Seo';
import Link from 'next/link';
import { HallOfFame } from '../components/main/HallOfFame';
import { ListLately } from '../components/main/ListLately';
import { CheerUp } from '../components/main/CheerUp';
import { CarouselMain } from '../components/main/CarouselMain';

const Calendar = dynamic<any>(
  () => import('../components/main/Calendar').then((mod) => mod.Calendar),
  {
    ssr: false,
  },
);

const Home: NextPage = () => {
  return (
    <>
      <Seo title="모락모락" />
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
              <div>
                <span className="text-2xl font-bold mr-2">
                  📆 채용 일정 캘린더
                </span>
                <Link href="/recruit">
                  <span className="text-xs hover:cursor-pointer relative">
                    더보기 ＞
                  </span>
                </Link>
              </div>
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
