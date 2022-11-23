/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-23
 * 개요:
   - 질문/답변에 대한 코멘트 리스트를 렌더링합니다.
 */

import { useRouter } from 'next/router';
import { useState } from 'react';
import { Comment } from './Comment';
import { TextArea } from './TextArea';
import useSWR from 'swr';
import { CommentProps } from '../../../libs/interfaces';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';

export const CommentList = () => {
  const router = useRouter();
  const { articleId } = router.query;
  const [isOpen, setIsOpen] = useState(false);
  const { data } = useSWR(`/articles/${articleId}`);
  const comments = data.article.comments;

  return (
    <div className="space-y-8 pt-4">
      {isOpen ? (
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
          <TextArea />
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
      )}
    </div>
  );
};
