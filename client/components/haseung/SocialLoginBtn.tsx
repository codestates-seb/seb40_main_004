/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-01(박혜정)
 * 개요: 소셜 로그인 버튼에 대한 컴포넌트입니다.
 */

import { faGoogle } from '@fortawesome/free-brands-svg-icons';
import { faComment } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import Link from 'next/link';

export const SocialLoginBtn = () => {
  return (
    <section className="flex w-full justify-center">
      <Link href="https://morak-873888559.ap-northeast-2.elb.amazonaws.com:8080/login/oauth2/code/google">
        <button className="bg-main-gray rounded-[20px] w-44 mr-2 h-10">
          <FontAwesomeIcon
            icon={faGoogle}
            className="cursor-pointer h-7 align-middle"
          />
        </button>
      </Link>
      <Link href="http://morak-873888559.ap-northeast-2.elb.amazonaws.com:8080/oauth2/authorization/kakao">
        <button className="bg-main-yellow w-44 mr-2 cursor-pointer rounded-[20px] h-10">
          <FontAwesomeIcon
            icon={faComment}
            className="cursor-pointer h-7 align-middle"
          />
        </button>
      </Link>
    </section>
  );
};
