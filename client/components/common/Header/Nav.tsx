import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

export const Nav = () => {
  const [pathname, setPathname] = useState('/');
  const router = useRouter();
  useEffect(() => {
    setPathname(router.pathname);
  }, []);
  return (
    <ul className="flex gap-4 items-baseline">
      <li>
        <Link href="/questions">
          <button
            className={`border-b-4 py-1 ${
              pathname.includes('/questions')
                ? 'border-main-yellow'
                : 'border-background-gray'
            }`}
          >
            질문/답변
          </button>
        </Link>
      </li>
      <li>
        <Link href="/informations">
          <button
            className={`border-b-4 py-1 ${
              pathname.includes('/informations')
                ? 'border-main-yellow'
                : 'border-background-gray'
            }`}
          >
            정보글
          </button>
        </Link>
      </li>
      <li>
        <Link href="/recruit">
          <button
            className={`border-b-4 py-1 ${
              pathname.includes('/recruit')
                ? 'border-main-yellow'
                : 'border-background-gray'
            }`}
          >
            채용 일정
          </button>
        </Link>
      </li>
    </ul>
  );
};
