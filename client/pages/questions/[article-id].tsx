/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-17
 * 개요
   - 질문 상세 페이지입니다.
   - 각 질문에 대한 정보, 본문, 답변과 댓글이 렌더링됩니다.
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
          <h2 className="text-main-yellow font-semibold text-2xl sm:text-3xl">
            A.
          </h2>
          <h2>1개의 답변이 달렸습니다.</h2>
        </section>
        <QusetionAnswer />
        <article className="flex justify-center mt-5">
          <button>더보기</button>
        </article>
        <article className="mt-10 border-b">
          <h2 className="text-xl sm:text-2xl font-bold pb-2">
            ✨ 당신의 지식을 공유해주세요!
          </h2>
        </article>
        <AnswerEditor />
      </main>
      <Footer />
    </>
  );
};

export default QuestionDetail;
