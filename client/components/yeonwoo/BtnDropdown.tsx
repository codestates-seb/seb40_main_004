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
            <li className="hover:bg-main-yellow py-1 pl-3 rounded-xl">
              <Link href="/dashboard">
                <button>대시보드</button>
              </Link>
            </li>
            <li className="hover:bg-main-yellow py-1 pl-3 rounded-xl">
              <Link href="/edit-profile">
                <button>정보 수정</button>
              </Link>
            </li>
            <li className="hover:bg-main-yellow py-1 pl-3 rounded-xl">
              <Link href="/edit-password">
                <button>비밀번호 변경</button>
              </Link>
            </li>
            <li className="hover:bg-main-yellow py-1 pl-3 rounded-xl">
              <button onClick={onClickLogout}>로그아웃</button>
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
