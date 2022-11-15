/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { UserNickname } from './QuestionContent/UserNickname';
import { CreatedDate } from './QuestionContent/CreatedDate';
import { ProfileImage } from './ProfileImage';

export const Comment = () => {
  return (
    <section className="flex w-full">
      <ProfileImage />
      <section className="flex w-full flex-col pl-4 space-y-3">
        <article className="w-full flex justify-between items-center">
          <UserNickname />
          <CreatedDate />
        </article>
        <p className="text-xs sm:text-base">
          Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin purus
          eros, mattis et pretium at, sagittis vel purus. Lorem ipsum dolor sit
          amet, consectetur adipiscing elit. Proin purus eros, mattis et pretium
          at, sagittis vel purus.
        </p>
        <article className="space-x-2 text-xs ml-auto">
          <button>수정</button>
          <button>삭제</button>
        </article>
      </section>
    </section>
  );
};
