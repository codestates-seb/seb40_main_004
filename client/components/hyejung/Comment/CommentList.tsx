/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-16
 * 개요:
   - 질문/답변에 대한 코멘트 리스트를 렌더링합니다.
 */

import { useState } from 'react';
import { Comment } from './Comment';
import { TextArea } from './TextArea';

import { useFetch } from '../../../libs/useFetch';
import { ArticleDetail } from '../../../mocks/types';

export const CommentList = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { data, isLoading } = useFetch('/api/articles/1');
  const content: ArticleDetail = data?.article;
  const comments = content?.comments;
  console.log(comments);

  if (isLoading) return <div>로딩중~</div>;
  return (
    <div className="space-y-8 pt-4">
      {isOpen ? (
        <>
          {comments.map((el) => (
            <Comment
              key={el.commentId}
              id={el.commentId}
              profileImage={el.avatar.remotePath}
              name={el.userInfo.nickname}
              userId={el.userInfo.userId}
              cretedAt={el.createdAt}
              content={el.content}
            />
          ))}
          <TextArea />
          <button onClick={() => setIsOpen(false)}>닫기</button>
        </>
      ) : (
        <>
          <Comment
            id={comments[0].commentId}
            profileImage={comments[0].avatar.remotePath}
            name={comments[0].userInfo.nickname}
            userId={comments[0].userInfo.userId}
            cretedAt={comments[0].createdAt}
            content={comments[0]?.content}
          />
          <button onClick={() => setIsOpen(true)}>펼치기</button>
        </>
      )}
    </div>
  );
};
