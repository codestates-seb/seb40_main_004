/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-23
 */

import { faUser } from '@fortawesome/free-regular-svg-icons';
import {
  faArrowRightFromBracket,
  faChevronDown,
  faChevronUp,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useState } from 'react';
import { useSetRecoilState } from 'recoil';
import { isLoginAtom } from '../../atomsYW';

export const BtnDropdown = () => {
  const [dropdown, setDropdown] = useState(false);
  const setIsLogin = useSetRecoilState(isLoginAtom);
  const router = useRouter();

  const onClickLogout = async () => {
    await axios.delete('/api/auth/token', {
      headers: {
        RefreshToken: localStorage.getItem('refreshToken'),
        'ngrok-skip-browser-warning': '111',
      },
    });
    localStorage.removeItem('email');
    localStorage.removeItem('userId');
    localStorage.removeItem('nickname');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setIsLogin(false);
    router.push('/');
  };

  const onClickDashboard = () => {
    console.log(localStorage.getItem('userId'));
  };

  return (
    <>
      {dropdown ? (
        <div className="relative">
          <button onClick={() => setDropdown((prev) => !prev)}>
            <FontAwesomeIcon icon={faChevronUp} size="lg" />
          </button>
          <ul className="border border-solid border-black border-opacity-10 border-spacing-1 right-0 w-[200px] rounded-xl absolute top-8 bg-background-gray z-20">
            <li className="pt-4 pb-1 mx-4 flex justify-between items-center border-b border-solid">
              <span className="text-xs">나의 모락</span>
              <span className="text-lg font-semibold">✨ 260 모락</span>
            </li>
            <Link href="/dashboard">
              <li
                className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer mt-2 py-1 px-4 rounded-xl text-[15px]"
                onClick={onClickDashboard}
              >
                <FontAwesomeIcon icon={faUser} size="sm" />
                <span className="ml-2">대시보드</span>
              </li>
            </Link>
            <Link href="/edit-profile">
              <li className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer py-1 mb-2 px-4 rounded-xl text-[15px]">
                <FontAwesomeIcon icon={faUser} size="sm" />
                <span className="ml-2">개인정보 수정</span>
              </li>
            </Link>
            <li
              className="hover:cursor-pointer py-4 px-4 text-[15px] border-t"
              onClick={onClickLogout}
            >
              <FontAwesomeIcon icon={faArrowRightFromBracket} size="sm" />
              <span className="ml-2">로그아웃</span>
            </li>
          </ul>
        </div>
      ) : (
        <button onClick={() => setDropdown((prev) => !prev)}>
          <FontAwesomeIcon icon={faChevronDown} size="lg" />
        </button>
      )}
    </>
  );
};
