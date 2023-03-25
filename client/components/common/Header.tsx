import { BtnUser } from './BtnUser';
import { BtnLogin } from './BtnLogin';
import { Logo } from './Logo';
import { Nav } from './Nav';
import { SearchBar } from './SearchBar';
import BtnDropdown from '../common';
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import { useEffect } from 'react';
import { client } from '@libs/client';

import { isLoginAtom } from '@atoms/loginAtom';
import { renderingAtom } from '@atoms/renderingAtom';
import { dataHeaderAtom } from '@atoms/userAtom';

import { IDataHeader } from '@type/user';

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
    <header className="flex items-center justify-between max-w-[1280px] mx-auto mobile:flex mobile:flex-col mobile:space-y-2">
      <div className="flex items-center w-6/12 mobile:flex mobile:flex-col mobile:items-center">
        <Logo />
        <Nav />
      </div>
      <div className="flex items-center w-3/12  mobile:w-72">
        <SearchBar />
      </div>
      <div className="flex items-center w-3/12 justify-end mobile:w-full mobile:flex  mobile:justify-center">
        {isLogin ? (
          <div className="flex gap-6 mobile:flex mobile:justify-center mobile:mb-4">
            <BtnUser />
            <BtnDropdown />
          </div>
        ) : (
          <BtnLogin />
        )}
      </div>
    </header>
  );
};
