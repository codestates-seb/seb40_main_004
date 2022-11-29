/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import Link from 'next/link';

export const Nav = () => {
  return (
    <ul className="flex gap-4">
      <li>
        <Link href="/questions">
          <button>질문/답변</button>
        </Link>
      </li>
    </ul>
  );
};
