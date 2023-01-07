/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-03
 */

import { BtnUser } from '../yeonwoo/BtnUser';
import { BtnLogin } from '../yeonwoo/BtnLogin';
import { Logo } from '../yeonwoo/Logo';
import { Nav } from '../yeonwoo/Nav';
import { SearchBar } from '../yeonwoo/SearchBar';
import { BtnDropdown } from '../yeonwoo/BtnDropdown';
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import { dataHeaderAtom, isLoginAtom, renderingAtom } from '../../atomsYW';
import { useEffect } from 'react';
import { client } from '../../libs/client';
import { IDataHeader } from '../../interfaces';

export const Header = () => {
  const [isLogin, setIsLogin] = useRecoilState(isLoginAtom);
  const rendering = useRecoilValue(renderingAtom);
  const setDataHeader = useSetRecoilState(dataHeaderAtom);
  const getDataHeader = async () => {
    if (isLogin) {
      const res = await client.get<IDataHeader>('/api/users/points');
      setDataHeader(res.data);
      res.data.avatar &&
        res.data.avatar.remotePath &&
        localStorage.setItem('avatarPath', res.data.avatar.remotePath);
      res.data.userInfo.grade &&
        localStorage.setItem('grade', res.data.userInfo.grade);
    }
  };
  useEffect(() => {
    if (typeof window !== 'undefined') {
      if (localStorage.getItem('refreshToken')) {
        setIsLogin(true);
      } else {
        setIsLogin(false);
      }
    } else {
      setIsLogin(false);
    }
    getDataHeader();
  }, [rendering]);
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
