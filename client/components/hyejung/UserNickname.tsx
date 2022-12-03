/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-21
 */

import Link from 'next/link';
import { UserInfo } from '../../libs/interfaces';

export const UserNickname = ({ nickname, userId, grade }: UserInfo) => {
  return (
    <Link href={`/dashboard/${userId}`}>
      <button className="font-bold text-sm sm:text-[16px] space-x-1">
        {grade === 'CANDLE' ? '🔥' : '💎'} {nickname}
      </button>
    </Link>
  );
};
