/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-30
 * 개요
   - 질문 상세 페이지입니다.
   - 각 질문에 대한 정보, 본문, 답변과 댓글이 렌더링됩니다.
 */

import { GetServerSideProps, NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { QuestionContent } from '../../components/hyejung/QuestionContent';
import { AnswerListContainer } from '../../components/hyejung/AnswerContainer';
import { AnswerEditor } from '../../components/hyejung/AnswerEditor';
import { useFetch } from '../../libs/useFetchSWR';
import { useRecoilState, useSetRecoilState } from 'recoil';
import { articleAuthorIdAtom, isAnswerPostedAtom } from '../../atomsHJ';
import { useEffect, useRef } from 'react';
import { BtnTopDown } from '../../components/common/BtnTopDown';

type QuestionDetailProps = {
  articleId: string;
};

const QuestionDetail: NextPage<QuestionDetailProps> = ({ articleId }) => {
  // 게시글 데이터
  const { data: articleData, isLoading: articleLoading } = useFetch(
    `/api/articles/${articleId}`,
  );

  // 답변 데이터
  const {
    data: answers,
    isLoading,
    isError,
  } = useFetch(`/api/articles/${articleId}/answers?page=1&size=5`);
  const answerData = !isLoading && answers.data;
  const answerCount = !isLoading && answers.pageInfo.totalElements;
  if (isError) console.log(isError);

  const setArticleAuthorId = useSetRecoilState(articleAuthorIdAtom);

  useEffect(() => {
    if (!articleLoading)
      setArticleAuthorId(articleData.userInfo.userId.toString());
  }, [articleLoading]);

  const [isAnswerPosted, setIsAnswerPosted] =
    useRecoilState(isAnswerPostedAtom);

  const answerCountEl = useRef<null | HTMLDivElement>(null);

  // 답변 작성 후 스크롤 상단으로 이동
  useEffect(() => {
    if (answerCountEl.current && isAnswerPosted)
      answerCountEl.current.scrollIntoView({ behavior: 'smooth' });
    setIsAnswerPosted(false);
  }, [isAnswerPosted]);

  return (
    <>
      <Header />
      <main className="max-w-[900px] mx-auto min-h-[80vh] bg-white p-[45px] sm:p-[60px] shadow-sm border-[1px] border-gray-200">
        <BtnTopDown />
        {!isLoading ? (
          <>
            <QuestionContent />
            <section className="flex w-full text-lg sm:text-xl space-x-2 items-center">
              {!isLoading && answerCount ? (
                <div className="flex flex-col w-full">
                  <div className="flex my-8 space-x-2" ref={answerCountEl}>
                    <h2 className="text-main-yellow font-semibold text-xl sm:text-2xl">
                      A.
                    </h2>
                    <h2 className="font-semibold text-xl sm:text-2xl">
                      {answerCount} 개의 답변이 달렸습니다.
                    </h2>
                  </div>
                  <AnswerListContainer
                    initialAnswers={answerData}
                    totalPages={answers.pageInfo.totalPages}
                  />
                </div>
              ) : (
                <div className="flex justify-center my-20 text-main-gray w-full text-base">
                  아직 작성된 답변이 없네요...🥲
                </div>
              )}
            </section>

            {articleData?.isClosed ? null : (
              <>
                <article className="mt-10 border-b">
                  <h2 className="text-xl sm:text-2xl font-bold pb-2">
                    ✨ 당신의 지식을 공유해주세요!
                  </h2>
                </article>
                <AnswerEditor />
              </>
            )}
          </>
        ) : (
          <div>Loading...</div>
        )}
      </main>
      <Footer />
    </>
  );
};

const Page: NextPage<{
  id: string;
}> = ({ id }) => {
  return (
    // 질문 본문에 대한 캐시 초기값 설정
    <QuestionDetail articleId={id} />
  );
};

export const getServerSideProps: GetServerSideProps = async (ctx) => {
  const id = ctx.params?.articleId;

  return {
    props: {
      id,
    },
  };
};

export default Page;
