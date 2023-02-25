import { faUser } from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

const links = [
  { href: '/edit-profile', text: '프로필 수정' },
  { href: '/edit-password', text: '비밀번호 수정' },
  { href: '/membership-withdrawal', text: '회원 탈퇴' },
];

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
      {links.map((link) => (
        <Link key={link.href} href={link.href}>
          <div
            className={`w-full px-4 py-3 flex items-baseline hover:cursor-pointer ${
              pathname === link.href
                ? 'border-main-yellow bg-[#D9D9D9] border-l-4'
                : 'border-transparent'
            }`}
          >
            <FontAwesomeIcon icon={faUser} />
            <span className="ml-4 text-lg">{link.text}</span>
          </div>
        </Link>
      ))}
    </>
  );
};
