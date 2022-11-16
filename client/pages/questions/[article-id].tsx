/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { QuestionContent } from '../../components/hyejung/QuestionContent/QuestionContent';
import { QusetionAnswer } from '../../components/hyejung/QuestionAnswer/QuestionAnswer';
import { AnswerEditor } from '../../components/hyejung/QuestionAnswer/AnswerEditor';

const QuestionDetail: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto min-h-[80vh] bg-white p-[60px] ">
        <QuestionContent />
        <section className="flex w-full text-lg sm:text-xl space-x-2 items-center">
          <h1 className="text-main-yellow font-semibold text-2xl sm:text-3xl">
            A.
          </h1>
          <h1>1개의 답변이 달렸습니다.</h1>
        </section>
        <QusetionAnswer />
        <article className="flex justify-center mt-5">
          <button>더보기</button>
        </article>
        <AnswerEditor />
      </main>
      <Footer />
    </>
  );
};

export default QuestionDetail;
