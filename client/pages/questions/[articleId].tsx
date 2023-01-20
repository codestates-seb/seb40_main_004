import { GetServerSideProps, NextPage } from 'next';
import { useEffect, useRef } from 'react';
import { useRecoilStateLoadable, useSetRecoilState } from 'recoil';
import axios from 'axios';
import { SWRConfig } from 'swr';

import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { Seo } from '../../components/common/Seo';

import { ArticleDetail } from '../../types/article';

import { useFetch } from '../../libs/useFetchSWR';

import { articleAuthorIdAtom } from '../../atoms/articleAtom';
import { isAnswerPostedAtom } from '../../atoms/answerAtom';
import { AnswerEditor } from '../../components/questions/question-detail/AnswerContent/AnswerEditor';
import { BtnTopDown } from '../../components/common/BtnTopDown';
import { QuestionContent } from '../../components/questions/question-detail/QuestionContent/QuestionContent';
import { AnswerListContainer } from '../../components/questions/question-detail/AnswerContent/AnswerContainer';

type QuestionDetailProps = {
  articleId: string;
  article: ArticleDetail;
};

const QuestionDetail = ({
  articleId,
  article: articleData,
}: QuestionDetailProps) => {
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
    setArticleAuthorId(articleData.userInfo.userId.toString());
  }, []);

  const [isAnswerPosted, setIsAnswerPosted] =
    useRecoilStateLoadable(isAnswerPostedAtom);

  const answerCountEl = useRef<null | HTMLDivElement>(null);

  // 답변 작성 후 스크롤 상단으로 이동
  useEffect(() => {
    if (answerCountEl.current && isAnswerPosted)
      answerCountEl.current.scrollIntoView({ behavior: 'smooth' });
    setIsAnswerPosted(false);
  }, [isAnswerPosted]);
  return (
    <>
      <Seo title={articleData.title} />
      <Header />
      <main className="max-w-[900px] mx-auto min-h-[80vh] bg-white  p-8 md:p-16 shadow-sm border-[1px] border-gray-200">
        <BtnTopDown />
        <QuestionContent articleId={articleId} article={articleData} />
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
        {!articleData?.isClosed && (
          <>
            <article className="mt-10 border-b">
              <h2 className="text-xl sm:text-2xl font-bold pb-2">
                ✨ 당신의 지식을 공유해주세요!
              </h2>
            </article>
            <AnswerEditor />
          </>
        )}
      </main>
      <Footer />
    </>
  );
};

const Page: NextPage<{
  article: ArticleDetail;
  id: string;
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
      <QuestionDetail articleId={id} article={article} />
    </SWRConfig>
  );
};

export const getServerSideProps: GetServerSideProps = async (ctx) => {
  const id = ctx.params?.articleId;
  const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

  // 질문 본문 요청
  const resArticle = await axios.get(`${BASE_URL}/articles/${id}`);
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
