/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-25
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
import { client } from '../../libs/client';
import { AnswerListProps, ArticleDetail } from '../../libs/interfaces';
import useSWR, { SWRConfig } from 'swr';
import { useFetch } from '../../libs/useFetchSWR';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { articleAuthorIdAtom } from '../../atomsHJ';
import { useEffect } from 'react';
import { isLoginAtom } from '../../atomsYW';

type QuestionDetailProps = {
  articleId: string;
};

const QuestionDetail: NextPage<QuestionDetailProps> = ({ articleId }) => {
  const { data } = useSWR(`/articles/${articleId}`);
  const articleData = data.article;

  const {
    data: answers,
    isLoading,
    isError,
  } = useFetch(`/api/articles/${articleId}/answers?page=1&size=5`);
  const answerData = answers?.data;
  const answerCount = answers?.pageInfo.totalElements;
  if (isError) console.log(isError);

  const setArticleAuthorId = useSetRecoilState(articleAuthorIdAtom);

  useEffect(() => {
    setArticleAuthorId(articleData.userInfo.userId.toString());
  }, []);

  return (
    <>
      <Header />
      <main className="max-w-[900px] mx-auto min-h-[80vh] bg-white p-[60px] shadow-sm border-[1px] border-gray-200">
        <QuestionContent />
        <section className="flex w-full text-lg sm:text-xl space-x-2 items-center">
          {answerCount ? (
            <>
              <div className="flex mt-10 space-x-2">
                <h2 className="text-main-yellow font-semibold text-xl sm:text-2xl">
                  A.
                </h2>
                <h2 className="font-semibold text-xl sm:text-2xl">
                  {answerCount} 개의 답변이 달렸습니다.
                </h2>
              </div>
            </>
          ) : (
            <div className="text-center">Loading...</div>
          )}
        </section>
        {answerData || !isLoading ? (
          <AnswerListContainer
            initialAnswers={answerData}
            totalPages={answers.pageInfo.totalPages}
          />
        ) : (
          <div className="flex justify-center my-20 text-main-gray">
            아직 작성된 답변이 없네요...🥲
          </div>
        )}
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

const Page: NextPage<{
  article: ArticleDetail;
  id: string;
  answer?: AnswerListProps | null;
}> = ({ article, id }) => {
  const keyArticle = `/articles/${id}`;
  return (
    // 질문 본문에 대한 캐시 초기값 설정
    <SWRConfig
      value={{
        fallback: {
          [keyArticle]: {
            article,
          },
        },
      }}
    >
      <QuestionDetail articleId={id} />
    </SWRConfig>
  );
};

export const getServerSideProps: GetServerSideProps = async (ctx) => {
  const id = ctx.params?.articleId;
  const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

  // 질문 본문 요청
  const resArticle = await client.get(`${BASE_URL}/articles/${id}`);
  const article = resArticle.data;

  // article 데이터가 존재하지 않으면 일단 404 페이지로 이동
  if (!article) {
    return {
      redirect: {
        destination: '/404',
        permanent: false,
      },
    };
  }
  return {
    props: {
      article,
      id,
    },
  };
};

export default Page;
