import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useRecoilValue } from 'recoil';
import { dataHeaderAtom } from '../../../atoms/userAtom';
import { changeGradeEmoji } from '../../../libs/changeGradeEmoji';

export const BtnUser = () => {
  const dataHeader = useRecoilValue(dataHeaderAtom);
  const [isValid, setIsValid] = useState(true);
  const [avatarPath, setAvatarPath] = useState<string | null>('/favicon.ico');
  const [nickname, setNickname] = useState<string | null>('');
  const [grade, setGrade] = useState<string | null>('');
  useEffect(() => {
    if (typeof window !== 'undefined') {
      setAvatarPath(
        dataHeader?.avatar
          ? dataHeader?.avatar.remotePath ?? localStorage.getItem('avatarPath')
          : localStorage.getItem('avatarPath') !== null
          ? localStorage.getItem('avatarPath') !== 'null'
            ? localStorage.getItem('avatarPath')
            : '/favicon.ico'
          : '/favicon.ico',
      );
      setNickname(
        dataHeader?.userInfo.nickname ?? localStorage.getItem('nickname'),
      );
      setGrade(dataHeader?.userInfo.grade ?? localStorage.getItem('grade'));
    }
  });
  return (
    <Link href={`/dashboard/${localStorage.getItem('userId')}`}>
      <button className="flex items-center">
        <div className="w-[35px] h-[35px] rounded-full overflow-hidden mr-4">
          {isValid ? (
            <Image
              src={avatarPath ?? '/favicon.cio'}
              width="35px"
              height="35px"
              onError={() => setIsValid(false)}
            />
          ) : (
            <Image src="/favicon.ico" width="25px" height="25px" />
          )}
        </div>
        <span className="">{changeGradeEmoji(grade ?? '')}</span>
        <span className="ml-2">{nickname}</span>
        <FontAwesomeIcon icon={faChevronRight} />
      </button>
    </Link>
  );
};
