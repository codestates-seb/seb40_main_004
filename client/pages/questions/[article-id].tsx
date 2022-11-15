/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { QuestionContent } from '../../components/hyejung/QuestionContent/QuestionContent';

const QuestionDetail: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto min-h-[80vh] bg-white">
        <QuestionContent />
      </main>
      <Footer />
    </>
  );
};

export default QuestionDetail;
