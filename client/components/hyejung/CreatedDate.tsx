/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-21
 */
type CreatedDateProps = {
  createdAt: string;
};

import { elapsedTime } from '../../libs/elapsedTime';

export const CreatedDate = ({ createdAt }: CreatedDateProps) => {
  return (
    <>
      <time className="text-main-gray text-xs sm:text-sm">
        {elapsedTime(createdAt)}
      </time>
    </>
  );
};
