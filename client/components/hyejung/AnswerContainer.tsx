/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-24
 * 최근 수정일: 2022-11-24
 */

import { useFetch } from '../../libs/useFetchSWR';
import { useState } from 'react';
import { useRouter } from 'next/router';
import useSWR from 'swr';
import { CommentContainer } from './CommentList';
import { AnswerProps } from './AnswerContent';
import { Answer } from '../../libs/interfaces';
import { AnswerContent } from './AnswerContent';

type AnswerListProps = {
  index: number;
};

type AnswerListContainerProps = {
  initialAnswers?: AnswerProps;
  totalPages: number;
};

// 답변 리스트 컴포넌트
export const AnswerList = ({ index }: AnswerListProps) => {
  // 현재 질문 id
  const router = useRouter();
  const { articleId } = router.query;

  // 게시글의 user 데이터와 채택 여부
  const { data: articleData } = useSWR(`/articles/${articleId}`);
  const userId = articleData.article.userInfo.userId;
  const isClosed = articleData.article.isClosed;

  // 답변 데이터
  const {
    data: answers,
    isLoading,
    isError,
  } = useFetch(`/api/articles/${articleId}/answers?page=${index}&size=5`);
  const answerData = answers?.data;

  if (!isLoading) {
    return answerData.map((answer: Answer) => (
      <AnswerContent
        key={answer.answerId}
        answer={answer}
        userId={userId}
        isClosed={isClosed}
      />
    ));
  } else {
    return <div className="text-center">Loading...</div>;
  }
};

// 답변 리스트 + 더보기버튼
export const AnswerListContainer = ({
  initialAnswers,
  totalPages,
}: AnswerListContainerProps) => {
  const [cnt, setCnt] = useState(2);
  const pages = [];

  if (initialAnswers) {
    pages.push(<AnswerList index={1} key={1} />);
  }

  for (let i = 2; i < cnt; i++) {
    pages.push(<AnswerList index={i} key={i} />);
  }
  return (
    <>
      {pages}
      {totalPages === cnt ? null : (
        <article className="flex justify-center my-16">
          <button onClick={() => setCnt(cnt + 1)}>더보기</button>
        </article>
      )}
    </>
  );
};
