/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-23
 */

import { BtnUser } from '../yeonwoo/BtnUser';
import { BtnLogin } from '../yeonwoo/BtnLogin';
import { Logo } from '../yeonwoo/Logo';
import { Nav } from '../yeonwoo/Nav';
import { SearchBar } from '../yeonwoo/SearchBar';
import { BtnDropdown } from '../yeonwoo/BtnDropdown';
import { useRecoilValue } from 'recoil';
import { isLoginAtom } from '../../atomsYW';

export const Header = () => {
  const isLogin = useRecoilValue(isLoginAtom);
  return (
    <header className="flex items-center justify-between max-w-[1280px] mx-auto">
      <div className="flex items-center w-6/12">
        <Logo />
        <Nav />
      </div>
      <div className="flex items-center w-3/12 justify-end">
        <SearchBar />
      </div>
      <div className="flex items-center w-3/12 justify-end">
        {isLogin ? (
          <div className="flex gap-6">
            <BtnUser />
            <BtnDropdown />
          </div>
        ) : (
          <>
            <BtnLogin />
          </>
        )}
      </div>
    </header>
  );
};
