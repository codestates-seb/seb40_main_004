/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-01(박혜정)
 * 개요: 소셜 로그인 버튼에 대한 컴포넌트입니다.
 */

import { faComment } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import Link from 'next/link';

export const SocialLoginBtn = () => {
  return (
    <section className="flex w-full justify-center">
      <Link href="http://morak-873888559.ap-northeast-2.elb.amazonaws.com:8080/oauth2/authorization/kakao">
        <button className="bg-gray-200 hover:bg-gray-300 cursor-pointer w-full p-3 rounded-[20px]">
          <FontAwesomeIcon
            icon={faComment}
            className="cursor-pointer h-6 items-center align-middle"
          />
        </button>
      </Link>
    </section>
  );
};
