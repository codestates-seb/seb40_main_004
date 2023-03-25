import { GetServerSideProps, NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { Footer } from '../../components/common/Footer';
import { QuestionContent } from '@components/questions/question-detail/QuestionContent/QuestionContent';
import { AnswerListContainer } from '@components/questions/question-detail/AnswerContent/AnswerContainer';
import { AnswerEditor } from '@components/questions/question-detail/AnswerContent/AnswerEditor';
import { useFetch } from '../../libs/useFetchSWR';
import { useRecoilState, useSetRecoilState } from 'recoil';
import { articleAuthorIdAtom } from '@atoms/articleAtom';
import { isAnswerPostedAtom } from '@atoms/answerAtom';
import { useEffect, useRef } from 'react';
import { BtnTopDown } from '../../components/common/BtnTopDown';
import { Loader } from '@components/common/Loader';

type QuestionDetailProps = {
  id: string;
};

const QuestionDetailPage: NextPage<QuestionDetailProps> = ({
  id: articleId,
}) => {
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
            <QuestionContent articleId={articleId} />
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
                  <AnswerListContainer />
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
          <div className="w-full h-full flex justify-center items-center">
            <Loader />
          </div>
        )}
      </main>
      <Footer />
    </>
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

export default QuestionDetailPage;
