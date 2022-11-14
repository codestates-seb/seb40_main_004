/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import Image from 'next/image';
import Link from 'next/link';

export const Logo = () => {
  return (
    <Link href="/">
      <button className="mr-6">
        <Image src="/vercel.svg" width="100px" height="60px"></Image>
      </button>
    </Link>
  );
};
