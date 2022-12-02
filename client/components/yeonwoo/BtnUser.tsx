/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-02
 */

import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useRecoilState } from 'recoil';
import { avatarPathAtom } from '../../atomsYW';

export const BtnUser = () => {
  const [avatarPath, setAvatarPath] = useRecoilState(avatarPathAtom);
  const [isValid, setIsValid] = useState(true);
  useEffect(() => {
    if (typeof window !== 'undefined') {
      const data = localStorage.getItem('avatarPath');
      if (data !== null) setAvatarPath(data !== 'null' ? data : '/favicon.ico');
      else setAvatarPath('/favicon.ico');
    }
  }, []);
  return (
    <Link href={`/dashboard/${localStorage.getItem('userId')}`}>
      <button className="flex items-center">
        <div className="w-[25px] h-[25px] rounded-full overflow-hidden">
          {isValid ? (
            <Image
              src={avatarPath}
              width="25px"
              height="25px"
              onError={() => setIsValid(false)}
            />
          ) : (
            <Image src="/favicon.ico" width="25px" height="25px" />
          )}
        </div>
        <span className="ml-2">{localStorage.getItem('nickname')}</span>
        <FontAwesomeIcon icon={faChevronRight} />
      </button>
    </Link>
  );
};
