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
import { isLoginAtom } from '../../../atoms/loginAtom';
import { dataHeaderAtom } from '../../../atoms/userAtom';

export const BtnDropdown = () => {
  const [dropdown, setDropdown] = useState(false);
  const [points, setPoints] = useState(0);
  const [dataHeader, setDataHeader] = useRecoilState(dataHeaderAtom);
  const setIsLogin = useSetRecoilState(isLoginAtom);
  const router = useRouter();

  const onClickLogout = async () => {
    if (confirm('로그아웃 하시겠습니까?')) {
      try {
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
      } catch (error) {
        alert('에러발생 : 다시 시도 부탁드립니다');
      }
    }
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
              <span className="text-sm font-semibold">{`✨ ${points} 모락`}</span>
            </li>
            <Link href={`/dashboard/${localStorage.getItem('userId')}`}>
              <li className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer mt-2 py-1 px-4 rounded-xl text-[15px]">
                <FontAwesomeIcon icon={faUser} size="sm" />
                <span className="ml-2 text-sm">대시보드</span>
              </li>
            </Link>
            <Link href="/edit-profile">
              <li className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer py-1 mb-2 px-4 rounded-xl text-[15px]">
                <FontAwesomeIcon icon={faUser} size="sm" />
                <span className="ml-2 text-sm">개인정보 수정</span>
              </li>
            </Link>
            <li
              className="hover:cursor-pointer py-4 px-4 text-[15px] border-t"
              onClick={onClickLogout}
            >
              <FontAwesomeIcon icon={faArrowRightFromBracket} size="sm" />
              <span className="ml-2 text-sm">로그아웃</span>
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
