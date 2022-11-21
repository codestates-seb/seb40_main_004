/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-21
 */

import { UserInfo } from '../../libs/interfaces';

export const UserNickname = ({ nickname, userId, grade }: UserInfo) => {
  return (
    <button className="font-bold text-sm sm:text-[16px] space-x-1">
      {grade === 'BRONZE' ? '🔥' : '💎'} {nickname}
    </button>
  );
};
