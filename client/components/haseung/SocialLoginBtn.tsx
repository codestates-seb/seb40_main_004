/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-01(박혜정)
 * 개요: 소셜 로그인 버튼에 대한 컴포넌트입니다.
 */

import { faGithub } from '@fortawesome/free-brands-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import KaKao from '../../public/kakao.png';

export const SocialLoginBtn = () => {
  const handleKaKaoClick = () => {
    // console.log('clicked');
  };

  const onClickComingSoon = () => {
    alert('Coming Soon...😸');
  };
  return (
    <section className="flex w-full justify-center">
      <button
        className="bg-main-gray rounded-[20px] w-44 mr-2"
        onClick={onClickComingSoon}
      >
        <FontAwesomeIcon icon={faGithub} className="cursor-pointer h-8" />
      </button>
      <Image
        onClick={onClickComingSoon}
        src={KaKao}
        alt="kakaologin"
        className="cursor-pointer rounded-[20px]"
        priority
      />
    </section>
  );
};
