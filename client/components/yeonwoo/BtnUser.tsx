/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-03
 */

import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useRecoilValue } from 'recoil';
import { dataHeaderAtom } from '../../atomsYW';

export const BtnUser = () => {
  const dataHeader = useRecoilValue(dataHeaderAtom);
  const [isValid, setIsValid] = useState(true);
  const [avatarPath, setAvatarPath] = useState<string | null>('/favicon.ico');
  const [nickname, setNickname] = useState<string | null>('');
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
    }
  });
  return (
    <Link href={`/dashboard/${localStorage.getItem('userId')}`}>
      <button className="flex items-center">
        <div className="w-[25px] h-[25px] rounded-full overflow-hidden">
          {isValid ? (
            <Image
              src={avatarPath ?? '/favicon.cio'}
              width="25px"
              height="25px"
              onError={() => setIsValid(false)}
            />
          ) : (
            <Image src="/favicon.ico" width="25px" height="25px" />
          )}
        </div>
        <span className="ml-2">{nickname}</span>
        <FontAwesomeIcon icon={faChevronRight} />
      </button>
    </Link>
  );
};
