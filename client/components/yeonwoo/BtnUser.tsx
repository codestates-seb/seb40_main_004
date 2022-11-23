/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-23
 */

import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import Link from 'next/link';
import { useRecoilValue } from 'recoil';
import { curUserAtom } from '../../atomsYW';
import { curUser } from '../../interfaces';

export const BtnUser = () => {
  const curUser = useRecoilValue<curUser>(curUserAtom);
  return (
    <Link href="/dashboard">
      <button className="flex items-center">
        <Image src="/favicon.ico" width="25px" height="25px" />
        <span className="ml-2">{curUser.nickname}</span>
        <FontAwesomeIcon icon={faChevronRight} />
      </button>
    </Link>
  );
};
