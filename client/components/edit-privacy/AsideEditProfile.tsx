import { faUser } from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

export const AsideEditProfile = () => {
  const [pathname, setPathname] = useState('');
  const router = useRouter();
  const getPathname = () => {
    setPathname(router.pathname);
  };
  useEffect(() => {
    getPathname();
  }, []);
  return (
    <>
      <Link href="/edit-profile">
        <div
          className={`w-full px-4 py-3 flex items-baseline hover:cursor-pointer ${
            pathname === '/edit-profile'
              ? 'border-main-yellow bg-[#D9D9D9] border-l-4'
              : 'border-transparent'
          }`}
        >
          <FontAwesomeIcon icon={faUser} />
          <span className="ml-4 text-lg">프로필 수정</span>
        </div>
      </Link>
      <Link href="edit-password">
        <div
          className={`w-full px-4 py-3 flex items-baseline hover:cursor-pointer ${
            pathname === '/edit-password'
              ? 'border-main-yellow bg-[#D9D9D9] border-l-4'
              : 'border-transparent'
          }`}
        >
          <FontAwesomeIcon icon={faUser} />
          <span className="ml-4 text-lg">비밀번호 수정</span>
        </div>
      </Link>
      <Link href="/membership-withdrawal">
        <div
          className={`w-full px-4 py-3 flex items-baseline hover:cursor-pointer ${
            pathname === '/membership-withdrawal'
              ? 'border-main-yellow bg-[#D9D9D9] border-l-4'
              : 'border-transparent'
          }`}
        >
          <FontAwesomeIcon icon={faUser} />
          <span className="ml-4 text-lg">회원 탈퇴</span>
        </div>
      </Link>
    </>
  );
};
