import { useRouter } from 'next/router';
import { Comment } from './Comment';
import { CommentTextArea } from './CommentTextArea';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';

import { CommentResp } from '@type/comment';
import { useFetch } from '@libs/useFetchSWR';
import { useState } from 'react';

type CommentList = {
  comments: CommentResp[];
};

const CommentList = ({ comments }: CommentList) => {
  const [isOpen, setIsOpen] = useState(false);

  return isOpen ? (
    <>
      {comments.map((comment: CommentResp) => (
        <Comment
          key={comment.commentId}
          commentId={comment.commentId}
          articleId={comment.articleId}
          content={comment.content}
          createdAt={comment.createdAt}
          lastModifiedAt={comment.lastModifiedAt}
          userInfo={comment.userInfo}
          avatar={comment.avatar}
          answerId={comment.answerId}
        />
      ))}
      <button className="text-base" onClick={() => setIsOpen(false)}>
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
        answerId={comments[0].answerId}
      />
      {comments.length > 1 && (
        <button className="text-base" onClick={() => setIsOpen(true)}>
          {`펼치기 `}
          <FontAwesomeIcon icon={faChevronDown} />
        </button>
      )}
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

  // 답변 코멘트 || 질문 코멘트에 따른 요청 url
  const url = answerId
    ? `/api/answers/${answerId}/comments`
    : `/api/articles/${articleId}/comments`;

  // 코멘트 조회 요청 로직
  const { data: comments } = useFetch(url) || [];

  return (
    <>
      <h3 className="text-xl font-bold">{comments?.length || 0} 코멘트</h3>
      <section className="space-y-8 pt-4">
        {comments?.length > 0 && <CommentList comments={comments} />}
        <CommentTextArea answerId={answerId} />
      </section>
    </>
  );
};
