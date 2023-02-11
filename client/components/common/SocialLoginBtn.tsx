import { faComment } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import Link from 'next/link';

export const SocialLoginBtn = () => {
  return (
    <section className="flex w-full justify-center">
      <Link href="https://morak.link/oauth2/authorization/kakao">
        <button className="bg-[#fee500] hover:bg-[#fada0a] cursor-pointer w-full p-3 rounded-[20px]">
          <FontAwesomeIcon
            icon={faComment}
            className="cursor-pointer h-6 items-center align-middle"
          />
        </button>
      </Link>
    </section>
  );
};
