import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

export const Nav = () => {
  const [pathname, setPathname] = useState('/');
  const router = useRouter();
  useEffect(() => {
    setPathname(router.pathname);
  }, []);

  const CATEGORIES = ['질문 / 답변', '정보글', '채용 일정'];

  return (
    <ul className="flex items-baseline space-x-5 mobile:w-96 mobile:space-x-5 mobile:flex mobile:items-center mobile:justify-center">
      {['/questions', '/informations', '/recruit'].map((path, idx) => (
        <li key={path} className="">
          <Link href={path}>
            <button
              className={`border-b-4 py-1 ${
                pathname.includes(path)
                  ? 'border-main-yellow'
                  : 'border-background-gray'
              }`}
            >
              {CATEGORIES[idx]}
            </button>
          </Link>
        </li>
      ))}
    </ul>
  );
};
