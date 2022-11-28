/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-28
 * 개요:
   - 질문/답변에 대한 코멘트를 렌더링합니다.
 */

import { UserNickname } from './UserNickname';
import { CreatedDate } from './CreatedDate';
import { ProfileImage } from './ProfileImage';

import { CommentResp } from '../../libs/interfaces';
import { client } from '../../libs/client';
import { mutate } from 'swr';
import { useState } from 'react';
import { EditCommentTextArea } from './EditCommentTextArea';

export const Comment = ({
  answerId,
  commentId,
  articleId,
  content,
  createdAt,
  lastModifiedAt,
  userInfo,
  avatar,
}: CommentResp) => {
  let currUserId: any = '';
  if (typeof window !== 'undefined') {
    currUserId = localStorage.getItem('userId');
  }
  const authorId = userInfo.userId;

  // 답변 코멘트 || 질문 코멘트에 따른 요청 url
  const url = answerId
    ? `/api/answers/${answerId}/comments/${commentId}`
    : `/api/articles/${articleId}/comments/${commentId}`;

  const mutateUrl = url.split(`/${commentId.toString()}`)[0];

  const onDelete = () => {
    if (confirm('정말 코멘트를 삭제하시겠습니까..?')) {
      client
        .delete(url)
        .then(() => {
          alert('삭제가 완료되었습니다!');
          mutate(mutateUrl);
        })
        .catch((err) => {
          console.error(err);
          alert('삭제에 실패했습니다.');
        });
    }
  };

  const [isEdit, setIsEdit] = useState(false);

  const onEdit = () => {
    setIsEdit(true);
  };

  if (isEdit)
    return (
      <EditCommentTextArea
        url={url}
        mutateUrl={mutateUrl}
        commentId={commentId}
        isEdit={isEdit}
        setIsEdit={setIsEdit}
      />
    );
  else
    return (
      <section className="flex w-full">
        <ProfileImage src={avatar.remotePath} />
        <section className="flex w-full flex-col pl-4 space-y-3">
          <article className="w-full flex justify-between items-center">
            <UserNickname
              nickname={userInfo.nickname}
              userId={userInfo.userId}
              grade={userInfo.grade}
            />
            <CreatedDate createdAt={createdAt} />
          </article>
          <p className="text-xs sm:text-base">{content}</p>
          {currUserId === authorId ? null : (
            <article className="space-x-2 text-xs ml-auto">
              <button onClick={onEdit}>수정</button>
              <button onClick={onDelete}>삭제</button>
            </article>
          )}
        </section>
      </section>
    );
};
