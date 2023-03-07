import { NextPage } from 'next';

import { Footer } from '@components/common/Footer';
import { Header } from '@components/common/Header';
import { Seo } from '@components/common/Seo';
import { ListRecruit } from '@components/recruit/ListRecruit';

const Recruit: NextPage = () => {
  return (
    <>
      <Seo title="채용 일정" />
      <Header />
      <main
        className="max-w-[1280px] mx-auto min-h-[80vh] bg-white p-[45px] sm:p-[60px] shadow-sm border-[1px] border-gray-200
      "
      >
        <header className="mb-16 text-center">
          <span className="text-3xl mr-2 font-bold">👨‍💻 채용 일정</span>
        </header>
        <main>
          <ListRecruit />
        </main>
      </main>
      <Footer />
    </>
  );
};

export default Recruit;
