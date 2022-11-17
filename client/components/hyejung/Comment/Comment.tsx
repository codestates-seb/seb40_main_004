/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-16
 * 개요:
   - 질문/답변에 대한 코멘트를 렌더링합니다.
 */

import { UserNickname } from '../QuestionContent/UserNickname';
import { CreatedDate } from '../QuestionContent/CreatedDate';
import { ProfileImage } from '../ProfileImage';

type CommentProps = {
  id: number;
  profileImage: string;
  name: string;
  userId: number;
  cretedAt: string;
  content: string;
};

export const Comment = ({
  id,
  profileImage,
  name,
  userId,
  cretedAt,
  content,
}: CommentProps) => {
  return (
    <section className="flex w-full">
      <ProfileImage src={profileImage} />
      <section className="flex w-full flex-col pl-4 space-y-3">
        <article className="w-full flex justify-between items-center">
          <UserNickname name={name} id={userId} />
          <CreatedDate createdAt={cretedAt} />
        </article>
        <p className="text-xs sm:text-base">{content}</p>
        <article className="space-x-2 text-xs ml-auto">
          <button>수정</button>
          <button>삭제</button>
        </article>
      </section>
    </section>
  );
};
