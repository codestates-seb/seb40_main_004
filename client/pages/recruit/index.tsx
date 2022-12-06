/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-12-07
 * 최근 수정일: 2022-12-07
 */

import { NextPage } from 'next';
import { Footer } from '../../components/common/Footer';
import { Header } from '../../components/common/Header';
import { Seo } from '../../components/common/Seo';
import { ListRecruit } from '../../components/yeonwoo/ListRecruit';

const Recruit: NextPage = () => {
  return (
    <>
      <Seo title="채용 일정" />
      <Header />
      <div className="w-[720px] mx-auto rounded-2xl bg-white py-10 px-14">
        <header className="mb-16 text-center">
          <span className="text-3xl mr-2 font-bold">👨‍💻 채용 일정</span>
        </header>
        <main>
          <ListRecruit />
        </main>
      </div>
      <Footer />
    </>
  );
};

export default Recruit;
