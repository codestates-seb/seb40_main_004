/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { UserNickname } from './UserNickname';
import { CreatedDate } from './CreatedDate';
import Image from 'next/image';

export const Comment = () => {
  return (
    <section className="flex w-full">
      <div className="">
        <Image src="/favicon.ico" width="40px" height="40px" />
      </div>
      <section className="flex w-full flex-col pl-3">
        <article className="w-full flex justify-between">
          <UserNickname />
          <CreatedDate />
        </article>
        <p className="text-xs sm:text-base">
          Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin purus
          eros, mattis et pretium at, sagittis vel purus. Lorem ipsum dolor sit
          amet, consectetur adipiscing elit. Proin purus eros, mattis et pretium
          at, sagittis vel purus.
        </p>
      </section>
    </section>
  );
};
