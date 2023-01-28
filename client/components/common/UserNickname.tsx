import { changeGradeEmoji } from '@libs/changeGradeEmoji';
import { UserInfo } from '@type/user';
import Link from 'next/link';

export const UserNickname = ({ nickname, userId, grade }: UserInfo) => {
  return (
    <Link href={`/dashboard/${userId}`}>
      <button className="font-bold text-sm sm:text-[16px] space-x-1">
        {changeGradeEmoji(grade)} {nickname}
      </button>
    </Link>
  );
};
