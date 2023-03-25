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
    <div className="flex-col space-y-1 items-end w-[90px] hidden md:flex">
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
