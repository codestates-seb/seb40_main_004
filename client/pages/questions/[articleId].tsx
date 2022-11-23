/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-21
 * 개요
   - 질문 상세 페이지입니다.
   - 각 질문에 대한 정보, 본문, 답변과 댓글이 렌더링됩니다.
 */

import { GetServerSideProps, NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { QuestionContent } from '../../components/hyejung/QuestionContent';
import { QuestionAnswerList } from '../../components/hyejung/QuestionAnswer/QuestionAnswer';
import { AnswerEditor } from '../../components/hyejung/QuestionAnswer/AnswerEditor';
import { client } from '../../libs/client';
import { ArticleDetail } from '../../libs/interfaces';
import useSWR, { SWRConfig } from 'swr';
import { useRouter } from 'next/router';

type QuestionDetailProps = {
  articleId: string;
};

const QuestionDetail: NextPage<QuestionDetailProps> = ({ articleId }) => {
  // 질문 데이터
  const { data: article } = useSWR(`/articles/${articleId}`);
  const articleData = article.article;

  // 답변 데이터
  const { data: answers } = useSWR(
    `/articles/${articleId}/answers?page={1}&size={5}`,
  );
  const answersData = answers;
  const answerCount = answersData?.length;

  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto min-h-[80vh] bg-white p-[60px] ">
        <QuestionContent />

        <section className="flex w-full text-lg sm:text-xl space-x-2 items-center">
          {answerCount ? (
            <>
              <h2 className="text-main-yellow font-semibold text-2xl sm:text-3xl">
                A.
              </h2>
              <h2 className="font-semibold text-2xl sm:text-3xl">
                {answerCount} 개의 답변이 달렸습니다.
              </h2>
            </>
          ) : null}
        </section>
        {answersData ? (
          <>
            <QuestionAnswerList />
            <article className="flex justify-center my-20">
              <button>더보기</button>
            </article>
          </>
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

const Page: NextPage<{ article: ArticleDetail; id: string }> = ({
  article,
  id,
}) => {
  const key = `/articles/${id}`;
  return (
    // 이 페이지 안에서 사용할 article 데이터를 캐시 초기값으로 설정합니다.
    // /articles 도 마찬가지로 추후 /articles/{id} 로 수정합니다.
    <SWRConfig
      value={{
        fallback: {
          [key]: {
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
  // 추후 api를 동적으로 요청할 경우 이 id를 /articles/{id} 와 같이 작성합니당~
  const id = ctx.params?.articleId;
  const response = await client.get(`/articles/${id}`);
  const article = response.data;

  // article 데이터가 존재하지 않으면 일단 404 페이지로 이동
  // (destination은 임시로 작성했습니다.^_^)
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
