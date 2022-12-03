/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-01(박혜정)
 * 개요: 소셜 로그인 버튼에 대한 컴포넌트입니다.
 */

import { faGoogle } from '@fortawesome/free-brands-svg-icons';
import { faComment } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { KakaoInit } from '../../kakaoinit';

export const SocialLoginBtn = () => {
  const onClickComingSoon = () => {
    // alert('Coming Soon...😸');

    const kakao = KakaoInit();
    kakao.Auth.authorize({
      redirectUri: 'http://localhost:3000/login/oauth2/code/kakao',
    });

    const REST_API_KEY = process.env.NEXT_PUBLIC_REST_API;
    const REDIRECT_URI = 'http://localhost:3000';

    // 카카오 로그인 구현
    kakao.Auth.login({
      success: () => {
        kakao.API.request({
          url: `/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code`, // 사용자 정보 가져오기
          success: (res: any) => {
            // 로그인 성공할 경우 정보 확인 후 /kakao 페이지로 push
            console.log(res);
          },
          fail: (error: any) => {
            console.log(error);
          },
        });
      },
      fail: (error: any) => {
        console.log(error);
      },
    });
  };
  return (
    <section className="flex w-full justify-center">
      <button
        className="bg-main-gray rounded-[20px] w-44 mr-2"
        onClick={onClickComingSoon}
      >
        <FontAwesomeIcon icon={faGoogle} className="cursor-pointer h-8" />
      </button>
      <button onClick={onClickComingSoon} className="cursor-pointer ">
        <FontAwesomeIcon
          icon={faComment}
          className="cursor-pointer bg-main-yellow rounded-[20px] h-8 w-44 mr-2"
        />
      </button>
    </section>
  );
};
