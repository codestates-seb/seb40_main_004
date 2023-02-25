import { useState } from 'react';
import { AnswerContent } from './index';

import { useFetch } from '@libs/useFetchSWR';

import { Answer } from '@type/answer';
import { toast } from 'react-toastify';
import { useGetArticleId } from 'hooks/useGetArticleId';

type AnswerListProps = {
  index: number;
};

// 답변 리스트 컴포넌트
export const AnswerList = ({ index }: AnswerListProps) => {
  const { articleId } = useGetArticleId();

  // 게시글 데이터
  const { data: article, isLoading: articleLoading } = useFetch(
    `/api/articles/${articleId}`,
  );
  const isClosed = articleLoading || article.isClosed;

  // 답변 데이터
  const {
    data: answers,
    isLoading,
    isError,
  } = useFetch(`/api/articles/${articleId}/answers?page=${index}&size=5`);
  const answerData = answers?.data;
  if (isError) toast.error(isError);

  if (!isLoading) {
    return answerData.map((answer: Answer) => (
      <AnswerContent
        key={answer.answerId}
        answer={answer}
        isClosed={isClosed}
        pageInfo={index}
      />
    ));
  } else {
    return <div className="text-center">Loading...</div>;
  }
};

// 답변 리스트 + 더보기버튼
export const AnswerListContainer = () => {
  const [cnt, setCnt] = useState(2);
  const pages = [];

  const { articleId } = useGetArticleId();

  const { data: initialAnswers } = useFetch(
    `/api/articles/${articleId}/answers?page=1&size=5`,
  );

  if (initialAnswers) {
    pages.push(<AnswerList index={1} key={1} />);
  }

  for (let i = 2; i < cnt; i++) {
    pages.push(<AnswerList index={i} key={i} />);
  }

  return (
    <>
      {pages}
      {initialAnswers.pageInfo.totalPages + 1 === cnt || (
        <article className="flex justify-center text-base">
          <button onClick={() => setCnt(cnt + 1)}>더보기</button>
        </article>
      )}
    </>
  );
};
