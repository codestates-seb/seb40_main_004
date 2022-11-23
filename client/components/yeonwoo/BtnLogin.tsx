/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Link from 'next/link';

export const BtnLogin = () => {
  return (
    <Link href="/login">
      <button className="ml-6 flex gap-2 items-center">
        <span>로그인</span>
        <FontAwesomeIcon icon={faChevronRight} />
      </button>
    </Link>
  );
};
