import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import Link from 'next/link';

export const BtnUser = () => {
  return (
    <Link href="/dashboard">
      <button className="flex items-center">
        <Image src="/favicon.ico" width="25px" height="25px" />
        <span className="ml-2">김코딩</span>
        <FontAwesomeIcon icon={faChevronRight} />
      </button>
    </Link>
  );
};
