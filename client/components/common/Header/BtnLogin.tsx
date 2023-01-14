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
