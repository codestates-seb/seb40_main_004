/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-03
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
import { useEffect, useState } from 'react';
import { useRecoilState, useSetRecoilState } from 'recoil';
import { dataHeaderAtom, isLoginAtom } from '../../atomsYW';

export const BtnDropdown = () => {
  const [dropdown, setDropdown] = useState(false);
  const [points, setPoints] = useState(0);
  const [dataHeader, setDataHeader] = useRecoilState(dataHeaderAtom);
  const setIsLogin = useSetRecoilState(isLoginAtom);
  const router = useRouter();

  const onClickLogout = async () => {
    await axios.delete('/api/auth/token', {
      headers: {
        RefreshToken: localStorage.getItem('refreshToken'),
        'ngrok-skip-browser-warning': '111',
      },
    });
    localStorage.clear();
    setIsLogin(false);
    setDataHeader(null);
    router.push('/');
  };

  const getPoints = () => {
    if (dataHeader) setPoints(dataHeader.point);
  };

  useEffect(() => {
    getPoints();
  }, [dataHeader]);

  return (
    <>
      {dropdown ? (
        <div className="relative">
          <button onClick={() => setDropdown((prev) => !prev)}>
            <FontAwesomeIcon icon={faChevronUp} size="lg" />
          </button>
          <ul className="border border-solid border-black border-opacity-10 border-spacing-1 right-0 min-w-[200px] rounded-xl absolute top-8 bg-background-gray z-20">
            <li className="pt-4 pb-1 mx-4 flex justify-between items-center border-b border-solid">
              <span className="text-xs">나의 모락</span>
              <span className="text-lg font-semibold">{`✨ ${points} 모락`}</span>
            </li>
            <Link href={`/dashboard/${localStorage.getItem('userId')}`}>
              <li className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer mt-2 py-1 px-4 rounded-xl text-[15px]">
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
