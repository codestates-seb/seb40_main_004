/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-16
 * 개요:
   - 질문/답변에 대한 코멘트를 렌더링합니다.
 */

import { UserNickname } from '../UserNickname';
import { CreatedDate } from '../CreatedDate';
import { ProfileImage } from '../ProfileImage';

import { CommentProps } from '../../../libs/interfaces';
/*
  commentId: number;
  articleId: number;
  content: string;
  createdAt: string;
  lastModifiedAt: string;
  userInfo: UserInfo;
  avatar: Avatar;
   */

export const Comment = ({
  commentId,
  articleId,
  content,
  createdAt,
  lastModifiedAt,
  userInfo,
  avatar,
}: CommentProps) => {
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
        <article className="space-x-2 text-xs ml-auto">
          {/* 로그인 후 user state를 확인하여 보임/숨김 처리 필요! */}
          <button>수정</button>
          <button>삭제</button>
        </article>
      </section>
    </section>
  );
};
