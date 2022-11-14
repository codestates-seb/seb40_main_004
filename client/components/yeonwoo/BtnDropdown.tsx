/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Link from 'next/link';
import { useState } from 'react';
import { useSetRecoilState } from 'recoil';
import { isLoggedInAtom } from '../../atoms';

export const BtnDropdown = () => {
  const setIsLoggedIn = useSetRecoilState(isLoggedInAtom);
  const [dropdown, setDropdown] = useState(false);

  const onClickLogout = () => {
    setIsLoggedIn(false);
  };

  return (
    <>
      {dropdown ? (
        <div className="">
          <button onClick={() => setDropdown((prev) => !prev)}>
            <FontAwesomeIcon icon={faChevronUp} />
          </button>
          <ul className="border border-solid border-black border-spacing-1 fixed top-14 right-[5%] w-28 rounded-xl">
            <Link href="/dashboard">
              <li className="hover:bg-main-yellow hover:cursor-pointer py-1 pl-3 rounded-xl">
                대시보드
              </li>
            </Link>
            <Link href="/edit-profile">
              <li className="hover:bg-main-yellow hover:cursor-pointer py-1 pl-3 rounded-xl">
                정보 수정
              </li>
            </Link>
            <Link href="/edit-password">
              <li className="hover:bg-main-yellow hover:cursor-pointer py-1 pl-3 rounded-xl">
                비밀번호 변경
              </li>
            </Link>
            <li
              className="hover:bg-main-yellow hover:cursor-pointer py-1 pl-3 rounded-xl"
              onClick={onClickLogout}
            >
              로그아웃
            </li>
          </ul>
        </div>
      ) : (
        <button onClick={() => setDropdown((prev) => !prev)}>
          <FontAwesomeIcon icon={faChevronDown} />
        </button>
      )}
    </>
  );
};
