import { UserNickname } from '../../../common/UserNickname';
import { CreatedDate } from '../../../common/CreatedDate';
import { ProfileImage } from '../AnswerContent/ProfileImage';

import { CommentResp } from '../../../../libs/interfaces';
import { client } from '../../../../libs/client';
import { mutate } from 'swr';
import { useState } from 'react';
import { EditCommentTextArea } from './EditCommentTextArea';
import { useRecoilValue } from 'recoil';
import { isLoginAtom } from '../../../../atoms/loginAtom';

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
  const isLogin = useRecoilValue(isLoginAtom);

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
      <section className="flex w-full pb-2">
        <ProfileImage src={avatar.remotePath} />
        <section className="flex w-full flex-col pl-4 space-y-2">
          <article className="w-full flex space-x-3 items-center pb-2">
            <UserNickname
              nickname={userInfo.nickname}
              userId={userInfo.userId}
              grade={userInfo.grade}
            />
            <CreatedDate createdAt={createdAt} />
          </article>
          <p className="text-xs sm:text-base">{content}</p>
          {currUserId === authorId.toString() && isLogin ? (
            <article className="space-x-2 text-xs ml-auto">
              <button onClick={onEdit}>수정</button>
              <button onClick={onDelete}>삭제</button>
            </article>
          ) : null}
        </section>
      </section>
    );
};
