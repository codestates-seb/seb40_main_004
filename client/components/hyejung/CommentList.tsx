/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-23
 */

import { useRouter } from 'next/router';
import { useState } from 'react';
import { Comment } from './Comment';
import { CommentTextArea } from './CommentTextArea';
import useSWR from 'swr';
import { CommentProps } from '../../libs/interfaces';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';
import { client } from '../../libs/client';

type CommentList = {
  comments: CommentProps;
};

const CommentList = ({ comments }: CommentList) => {
  const [isOpen, setIsOpen] = useState(false);
  return isOpen ? (
    <>
      {comments.map((comment: CommentProps) => (
        <Comment
          key={comment.commentId}
          commentId={comment.commentId}
          articleId={comment.articleId}
          content={comment.content}
          createdAt={comment.createdAt}
          lastModifiedAt={comment.lastModifiedAt}
          userInfo={comment.userInfo}
          avatar={comment.avatar}
        />
      ))}
      <CommentTextArea />
      <button onClick={() => setIsOpen(false)}>
        {`닫기 `}
        <FontAwesomeIcon icon={faChevronUp} />
      </button>
    </>
  ) : (
    <>
      <Comment
        key={comments[0].commentId}
        commentId={comments[0].commentId}
        articleId={comments[0].articleId}
        content={comments[0].content}
        createdAt={comments[0].createdAt}
        lastModifiedAt={comments[0].lastModifiedAt}
        userInfo={comments[0].userInfo}
        avatar={comments[0].avatar}
      />
      <button onClick={() => setIsOpen(true)}>
        {`펼치기 `}
        <FontAwesomeIcon icon={faChevronDown} />
      </button>
    </>
  );
};

type CommentContainerProps = {
  answerId?: number;
};

export const CommentContainer = ({ answerId }: CommentContainerProps) => {
  // 현재 게시글 id
  const router = useRouter();
  const { articleId } = router.query;

  // api 요청 로직 완료 전 임시 코멘트
  const comments: any = [];

  // 답변 코멘트 || 질문 코멘트에 따른 요청 url
  // const url = answerId
  //   ? `/articles/${articleId}/answers/comments`
  //   : `/articles/${articleId}/comments`;

  // 코멘트 조회 요청 로직
  // const fetcher = async (url: string) => await client.get(url);
  // const { data } = useSWR(url, fetcher);
  // const comments = data.article.comments;

  // 펼치기 상태

  return (
    <div className="space-y-8 pt-4">
      {comments.length ? (
        <CommentList comments={comments} />
      ) : (
        <div>
          <CommentTextArea />
        </div>
      )}
    </div>
  );
};
