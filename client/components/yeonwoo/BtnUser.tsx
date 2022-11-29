/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-24
 */

import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import Link from 'next/link';

export const BtnUser = () => {
  return (
    <Link href={`/dashboard/${localStorage.getItem('userId')}`}>
      <button className="flex items-center">
        <div className="w-[25px] h-[25px] rounded-full overflow-hidden">
          <Image
            src={
              localStorage.getItem('avatarPath')
                ? `${localStorage.getItem('avatarPath')}`
                : '/favicon.ico'
            }
            width="25px"
            height="25px"
          />
        </div>
        <span className="ml-2">{localStorage.getItem('nickname')}</span>
        <FontAwesomeIcon icon={faChevronRight} />
      </button>
    </Link>
  );
};
