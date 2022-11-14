/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 * 개요 : footer 에서 개발자 이름을 표시하고 해당 개발자 깃허브, 블로그로 이동할 수 있는 컴포넌트입니다.
 */

type DeveloperInfoProps = {
  github: string;
  blog: string;
  children: string;
};

import Link from 'next/link';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGithub } from '@fortawesome/free-brands-svg-icons';
import { faBloggerB } from '@fortawesome/free-brands-svg-icons';

export const DeveloperLink = ({
  github,
  blog,
  children,
}: DeveloperInfoProps) => {
  return (
    <div className="flex-col space-y-1 items-center w-[90px] hidden md:flex">
      <span className="font-bold text-[18px]">{children}</span>
      <div className="space-x-2.5">
        <Link href={github}>
          <FontAwesomeIcon icon={faGithub} className="fa-lg cursor-pointer" />
        </Link>
        <Link href={blog}>
          <FontAwesomeIcon icon={faBloggerB} className="fa-lg cursor-pointer" />
        </Link>
      </div>
    </div>
  );
};
